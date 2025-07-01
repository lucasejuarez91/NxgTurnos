package com.ar.nxg.nxgappts.controller;

import com.ar.nxg.nxgappts.domain.*;
import com.ar.nxg.nxgappts.enums.ItemStatusEnum;
import com.ar.nxg.nxgappts.enums.PaymentStatusEnum;
import com.ar.nxg.nxgappts.service.AppointmentService;
import com.ar.nxg.nxgappts.service.PaymentItemService;
import com.ar.nxg.nxgappts.service.PaymentService;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/payment")
public class PaymentController extends GlobalControllerAdvice {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentItemService paymentItemService;

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping(value = "/payBooking")
    public String createBookingPayment(@RequestParam(value = "bookingCode") String bookingCode) throws MPException {
        try {
            Appointment appt = appointmentService.getAppointmentByCode(bookingCode);
            Payment payment = paymentService.findByAppointmentAndPaymentStatusEnum(appt, PaymentStatusEnum.PENDING);
            if(payment == null) {
                return "redirect:/appointments/confirmation?bookingId=" + bookingCode;
            }
            Preference preference = paymentService.generatePaymentMP(payment);
            return "redirect:" + preference.getInitPoint();
        } catch (MPApiException e) {
            //e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    @GetMapping("/paymentReserveConfirmation")
    public String paymentReserveConfirmation(
            @RequestParam String external_reference,
            @RequestParam String status) {
        try {
            Payment payment = paymentService.findByPaymentCode(external_reference);
            if(payment == null){
                return "public/appointments/list";
            }
            // Si el pago fue exitoso, verificamos el estado en Mercado Pago
            if ("approved".equals(status)) {
                com.mercadopago.resources.payment.Payment payMP = paymentService.paymentValidation(payment);
                if ("APPROVED".equalsIgnoreCase(payMP.getStatus())) {
                    // Marcar la reserva como confirmada en la BD
                    List<PaymentItem> pItems = payment.getPaymentItems();
                    pItems.forEach(paymentItem -> {
                        paymentItem.setItemStatusEnum(ItemStatusEnum.PAYED);
                        paymentItemService.saveOrUpdate(paymentItem);
                    });
                    payment.setPaymentStatusEnum(PaymentStatusEnum.APPROVED);
                    paymentService.saveOrUpdate(payment);
                    appointmentService.confirmAppointment(payment.getAppointment());
                    appointmentService.sendConfirmationMail(payment.getAppointment());
                }
            }
            // Si el pago está pendiente, la reserva sigue en "suspenso"
            return "redirect:/appointments/confirmation?bookingId=" + payment.getAppointment().getCode();
        } catch (MPApiException | MPException e) {
            return "public/booking/confirmation";
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }


}
