package com.ar.nxg.nxgappts.controller;

import com.ar.nxg.nxgappts.domain.*;
import com.ar.nxg.nxgappts.enums.AppointmentStatusEnum;
import com.ar.nxg.nxgappts.enums.ItemStatusEnum;
import com.ar.nxg.nxgappts.enums.PaymentMethodEnum;
import com.ar.nxg.nxgappts.enums.PaymentStatusEnum;
import com.ar.nxg.nxgappts.repositories.*;
import com.ar.nxg.nxgappts.service.AppointmentService;
import com.ar.nxg.nxgappts.service.PaymentService;
import com.mercadopago.*;

import com.mercadopago.client.payment.PaymentClient;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Controller
@RequestMapping("/payment")
public class PaymentController extends GlobalControllerAdvice {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentItemRepository paymentItemRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping(value = "/payBooking")
    public String createBookingPayment(@RequestParam(value = "apptId") long apptId) throws MPException {
        try {
            Appointment appt = appointmentRepository.findById(apptId).orElseThrow();
            List<Payment> payments = appt.getPayments();
            if(!payments.isEmpty()){
                for (Payment p : payments){
                    List<PaymentItem> paymentItems = p.getPaymentItems();
                    for (PaymentItem pItem : paymentItems){
                        if(pItem.getItemStatusEnum() == ItemStatusEnum.PENDING){
                            Item item = pItem.getItem();
                            if(item.getName().equalsIgnoreCase("RESERVATION")){
                                Preference preference = paymentService.generatePaymentMP(appt, p.getPaymentCode());
                                return "redirect:" + preference.getInitPoint();
                            }
                        }
                    }
                }
            }
            Payment payment = new Payment();
            payment.setPaymentMethodEnum(PaymentMethodEnum.MERCADOPAGO);
            payment.setPaymentStatusEnum(PaymentStatusEnum.PENDING);
            payment.setAppointment(appt);
            payment.setCurrency("ARS");
            //Item Reserva
            Item item = itemRepository.findByName("RESERVATION");
            //PaymentItem reserva
            PaymentItem paymentItem = getPaymentItem(payment, appt, item);
            //Sumar los valores de items
            payment.setAmount(paymentItem.getTotalPrice());
            paymentRepository.save(payment);
            paymentItemRepository.save(paymentItem);
            Preference preference = paymentService.generatePaymentMP(appt, payment.getPaymentCode());
            return "redirect:" + preference.getInitPoint();
        } catch (MPApiException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    private PaymentItem getPaymentItem(Payment payment, Appointment appt, Item item) {
        PaymentItem paymentItem = new PaymentItem();
        paymentItem.setPayment(payment);
        paymentItem.setQuantity(1);
        BigDecimal price = appt.getCompany().getPrepaymentExplicitValue().compareTo(BigDecimal.ZERO) != 0
                ? appt.getCompany().getPrepaymentExplicitValue()
                : appt.getService().getPrice()
                .multiply(BigDecimal.valueOf(appt.getCompany().getPrepaymentPercentage()))
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
        paymentItem.setTotalPrice(price);
        paymentItem.setCurrency("ARS");
        paymentItem.setItemStatusEnum(ItemStatusEnum.PENDING);
        paymentItem.setItem(item);
        return paymentItem;
    }

    @GetMapping("/paymentConfirmation")
    public String paymentConfirmation(
            @RequestParam String external_reference,
            @RequestParam String status,
            @RequestParam(required = false) Long payment_id) {

        try {
            Payment p = paymentRepository.findByPaymentCode(external_reference);
            if(p == null){
                return "public/booking/list";
            }
            // Si el pago fue exitoso, verificamos el estado en Mercado Pago
            if ("approved".equals(status)) {
                //Payment p = paymentRepository.findByPaymentCode(external_reference);
                Company company = p.getAppointment().getCompany();
                //MercadoPagoConfig.setAccessToken("APP_USR-8841472246820748-033006-68d7a92b26cf3c4b5951a74061d00360-2358436049");
                MercadoPagoConfig.setAccessToken(company.getAccessTokenMP());
                PaymentClient client = new PaymentClient();
                //Long paymentId = payment_id;
                client.get(payment_id);
                com.mercadopago.resources.payment.Payment pay = client.get(payment_id);

                if ("approved".equals(pay.getStatus())) {
                    // Marcar la reserva como confirmada en la BD
                    List<PaymentItem> pItems = p.getPaymentItems();
                    pItems.forEach(paymentItem -> {
                        paymentItem.setItemStatusEnum(ItemStatusEnum.PAYED);
                        paymentItemRepository.save(paymentItem);
                    });
                    p.setPaymentStatusEnum(PaymentStatusEnum.APPROVED);
                    paymentRepository.save(p);
                    confirmBooking(p.getAppointment(), payment_id);
                    appointmentService.sendConfirmationMail(p.getAppointment());
                    //return "/booking/confirmation";
                }
            }
            // Si el pago está pendiente, la reserva sigue en "suspenso"
            return "redirect:/booking/confirmation?bookingId=" + p.getAppointment().getCode();
        } catch (MPApiException | MPException e) {
            //e.printStackTrace();
            //return ResponseEntity.status(500).body("Error verificando el pago");
            return "public/booking/confirmation";
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private void confirmBooking(Appointment appt, long paymentId) {
        // Aquí actualizas la base de datos para confirmar la reserva
        //Appointment appt = appointmentRepository.findById(apptId).orElseThrow();
        appt.setApptStatus(AppointmentStatusEnum.CONFIRM);
        appointmentRepository.save(appt);
        System.out.println("Reserva " + appt.getCode() + " confirmada con pago: " + paymentId);
    }
}
