package com.example.assignment_transaction_flow.service.strategy;

import com.example.assignment_transaction_flow.model.PaymentMethod;
import org.springframework.stereotype.Component;

@Component
public class PaymentProcessorFactory {

    private final CardPaymentProcessor cardProcessor;
    private final UpiPaymentProcessor upiProcessor;
    private final WalletPaymentProcessor walletProcessor;

    public PaymentProcessorFactory(CardPaymentProcessor cardProcessor,
                                   UpiPaymentProcessor upiProcessor,
                                   WalletPaymentProcessor walletProcessor) {
        this.cardProcessor = cardProcessor;
        this.upiProcessor = upiProcessor;
        this.walletProcessor = walletProcessor;
    }

    public PaymentProcessor getProcessor(PaymentMethod method) {
        return switch (method) {
            case CARD -> cardProcessor;
            case UPI -> upiProcessor;
            case WALLET -> walletProcessor;
        };
    }
}
