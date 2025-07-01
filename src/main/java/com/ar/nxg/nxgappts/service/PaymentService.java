package com.ar.nxg.nxgappts.service;

import com.ar.nxg.nxgappts.domain.Appointment;
import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.Payment;
import com.ar.nxg.nxgappts.domain.PaymentItem;
import com.ar.nxg.nxgappts.enums.ItemStatusEnum;
import com.ar.nxg.nxgappts.enums.PaymentMethodEnum;
import com.ar.nxg.nxgappts.enums.PaymentStatusEnum;
import com.ar.nxg.nxgappts.repositories.PaymentRepository;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.PhoneRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.net.MPSearchRequest;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Service
public class PaymentService extends BaseService<Payment> {

    @Value("${server.servlet.context-path}") private String context;
    @Value("${server.address}") private String address;
    @Value("${server.port}") private String port;

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    private String buildContextPath(){
        return address + ":" + port + "/" + context;
    }

    private PreferenceRequest defaultPreferenceRequest(Payment payment, List<PreferenceItemRequest> items){
        List<PreferencePaymentTypeRequest> excludedPaymentTypes = new ArrayList<>();
        excludedPaymentTypes.add(PreferencePaymentTypeRequest.builder().id("ticket").build());

        List<PreferencePaymentMethodRequest> excludedPaymentMethods = new ArrayList<>();
        excludedPaymentMethods.add(PreferencePaymentMethodRequest.builder().id("").build());

        return PreferenceRequest.builder()
                .backUrls(
                        PreferenceBackUrlsRequest.builder()
                                .success(buildContextPath() + "/payment/paymentReserveConfirmation")
                                .failure(buildContextPath() + "/payment/failurePay")
                                .pending(buildContextPath() + "/payment/pendingPay")
                                .build())
                .differentialPricing(
                        PreferenceDifferentialPricingRequest.builder()
                                .id(1L)
                                .build())
                .expires(false)
                .items(items)
                .marketplaceFee(new BigDecimal("0"))
                .payer(
                        PreferencePayerRequest.builder()
                                .name(payment.getAppointment().getClient().getFirstName())
                                .surname(payment.getAppointment().getClient().getLastName())
                                .email(payment.getAppointment().getClient().getEmail())
                                .phone(PhoneRequest.builder().areaCode("11").number(payment.getAppointment().getClient().getPhone()).build())
                                .build())
                //.additionalInfo("Discount: 12.00")
                .autoReturn("all")
                .binaryMode(true)
                .externalReference(payment.getPaymentCode())
                .marketplace("marketplace")
                //.notificationUrl("https://notificationurl.com")
                .operationType("regular_payment")
                .paymentMethods(
                        PreferencePaymentMethodsRequest.builder()
                                .defaultPaymentMethodId("master")
                                .excludedPaymentTypes(excludedPaymentTypes)
                                .excludedPaymentMethods(excludedPaymentMethods)
                                .installments(5)
                                .defaultInstallments(1)
                                .build())
                .build();
    }

    public Preference generatePaymentMP(Payment payment) throws MPException, MPApiException {
        MercadoPagoConfig.setAccessToken(payment.getAppointment().getCompany().getAccessTokenMP());
        PreferenceClient client = new PreferenceClient();
        List<PreferenceItemRequest> items = new ArrayList<>();
        List<PaymentItem> itemsPending = getAllPendingItemsToPayByApptAndItemsStatus(payment.getAppointment(), ItemStatusEnum.PENDING);
        for (PaymentItem pItem :itemsPending){
            PreferenceItemRequest itemRequest =
                    PreferenceItemRequest.builder()
                            .id(String.valueOf(pItem.getId()))
                            .title(pItem.getItem().getName())
                            .description(pItem.getItem().getDescription())
                            .pictureUrl("")
                            .categoryId(pItem.getItem().getCategory().name())
                            .quantity(pItem.getQuantity())
                            .currencyId(pItem.getCurrency().name())
                            .unitPrice(pItem.getPrice())
                            .build();
            items.add(itemRequest);
        }
        PreferenceRequest preferenceRequest = defaultPreferenceRequest(payment, items);
        return client.create(preferenceRequest);
    }

    public List<PaymentItem> getAllPendingItemsToPayByApptAndItemsStatus(Appointment appt, ItemStatusEnum itemStatusEnum){
        List<Payment> payments = paymentRepository.findByAppointment(appt);
        List<PaymentItem> items = payments.stream()
                .flatMap(payment -> payment.getPaymentItems().stream()) // Aplana las listas de PaymentItems
                .toList(); // Recoge todos los PaymentItems en una sola lista
        if(itemStatusEnum != null){
            items = items.stream().filter(paymentItem -> paymentItem.getItemStatusEnum() == itemStatusEnum).toList();
        }
        return items;
    }

    public void addTotalizersInModelByAppt(Appointment appt, Model model){
        List<PaymentItem> itemsPending = getAllPendingItemsToPayByApptAndItemsStatus(appt, ItemStatusEnum.PENDING);
        BigDecimal itemsPendingValue = new BigDecimal(0);
        for (PaymentItem paymentItem : itemsPending) {
            itemsPendingValue = itemsPendingValue.add(paymentItem.getTotalPrice());
        }
        model.addAttribute("totalPending", itemsPendingValue);
        List<PaymentItem> itemsPayed = getAllPendingItemsToPayByApptAndItemsStatus(appt, ItemStatusEnum.PAYED);
        BigDecimal itemsPayedValue = new BigDecimal(0);
        for (PaymentItem paymentItem : itemsPayed) {
            itemsPayedValue = itemsPayedValue.add(paymentItem.getTotalPrice());
        }
        model.addAttribute("totalPayed", itemsPayedValue);
        BigDecimal total = itemsPendingValue.add(itemsPayedValue);
        model.addAttribute("total", total);
    }


    public com.mercadopago.resources.payment.Payment paymentValidation(Payment payment) throws MPException, MPApiException {
        Company company = payment.getAppointment().getCompany();
        MercadoPagoConfig.setAccessToken(company.getAccessTokenMP());
        PaymentClient client = new PaymentClient();
        Map<String, Object> filters = new HashMap<>();
        filters.put("sort", "date_created");
        filters.put("criteria", "desc");
        filters.put("external_reference", payment.getPaymentCode());
        filters.put("range", "date_created");
        filters.put("begin_date", "NOW-30DAYS");
        filters.put("end_date", "NOW");

        MPSearchRequest searchRequest =
                MPSearchRequest.builder().offset(0).limit(30).filters(filters).build();

        return client.search(searchRequest).getResults().get(0);
    }

    public Payment findByOrCreate(Appointment appt, PaymentStatusEnum paymentStatusEnum) {
        Payment payment = paymentRepository.findByAppointmentAndPaymentStatusEnum(appt,paymentStatusEnum);
        if(payment == null) {
            payment = new Payment();
            payment.setPaymentStatusEnum(PaymentStatusEnum.PENDING);
            payment.setPaymentMethodEnum(PaymentMethodEnum.MERCADOPAGO);
            payment.setAmount(new BigDecimal(0));
            payment.setAppointment(appt);
            payment.setCurrency("ARS");
            saveOrUpdate(payment);
        }
        return payment;
    }

    public Payment findByAppointmentAndPaymentStatusEnum(Appointment appt, PaymentStatusEnum paymentStatusEnum) {
        return paymentRepository.findByAppointmentAndPaymentStatusEnum(appt, paymentStatusEnum);
    }

    public Payment findByPaymentCode(String externalReference) {
        return paymentRepository.findByPaymentCode(externalReference);
    }
}
