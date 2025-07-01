package com.ar.nxg.nxgappts.service;

import com.ar.nxg.nxgappts.domain.Appointment;
import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.Payment;
import com.ar.nxg.nxgappts.domain.PaymentItem;
import com.ar.nxg.nxgappts.enums.ItemStatusEnum;
import com.ar.nxg.nxgappts.enums.PaymentStatusEnum;
import com.ar.nxg.nxgappts.repositories.PaymentRepository;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.PhoneRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentItemService extends BaseService<PaymentItem> {

}
