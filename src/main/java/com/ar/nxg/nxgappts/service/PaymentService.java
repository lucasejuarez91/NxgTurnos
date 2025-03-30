package com.ar.nxg.nxgappts.service;

import com.ar.nxg.nxgappts.domain.Appointment;
import com.ar.nxg.nxgappts.enums.AppointmentStatusEnum;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.PhoneRequest;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {

    @Value("${server.servlet.context-path}") private String context;

    public Preference generatePaymentMP(Appointment appt, String paymentCode) throws MPException, MPApiException {
        MercadoPagoConfig.setAccessToken("APP_USR-8841472246820748-033006-68d7a92b26cf3c4b5951a74061d00360-2358436049");
        PreferenceClient client = new PreferenceClient();
        BigDecimal price = appt.getCompany().getPrepaymentExplicitValue().compareTo(BigDecimal.ZERO) != 0
                ? appt.getCompany().getPrepaymentExplicitValue()
                : appt.getService().getPrice()
                .multiply(BigDecimal.valueOf(appt.getCompany().getPrepaymentPercentage()))
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);

        PreferenceItemRequest itemRequest =
                PreferenceItemRequest.builder()
                        .id(appt.getService().getCode())
                        .title(appt.getPaymentTitle(true))
                        .description(appt.getService().getName())
                        .pictureUrl(appt.getCompany().getLogo().getPath())
                        .categoryId("Service")
                        .quantity(1)
                        .currencyId("ARS")
                        .unitPrice(price)
                        .build();

        List<PreferenceItemRequest> items = new ArrayList<>();
        items.add(itemRequest);

        PreferenceFreeMethodRequest freeMethod =
                PreferenceFreeMethodRequest.builder()
                        .id(1L).build();
        List<PreferenceFreeMethodRequest> freeMethodList = new ArrayList<>();
        freeMethodList.add(freeMethod);

        List<PreferencePaymentTypeRequest> excludedPaymentTypes = new ArrayList<>();
        excludedPaymentTypes.add(PreferencePaymentTypeRequest.builder().id("ticket").build());

        List<PreferencePaymentMethodRequest> excludedPaymentMethods = new ArrayList<>();
        excludedPaymentMethods.add(PreferencePaymentMethodRequest.builder().id("").build());

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .backUrls(
                        PreferenceBackUrlsRequest.builder()
                                .success(context + "/payment/paymentConfirmation")
                                .failure(context + "/payment/failurePay")
                                .pending(context + "/payment/pendingPay")
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
                                .name(appt.getClient().getFirstName())
                                .surname(appt.getClient().getLastName())
                                .email(appt.getClient().getEmail())
                                .phone(PhoneRequest.builder().areaCode("11").number(appt.getClient().getPhone()).build())
                                .build())
                //.additionalInfo("Discount: 12.00")
                .autoReturn("all")
                .binaryMode(true)
                .externalReference(paymentCode)
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

        return client.create(preferenceRequest);
    }
}
