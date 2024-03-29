package br.finax.services;

import br.finax.models.CreditCard;
import br.finax.models.Invoice;
import br.finax.dto.InvoiceMonthValues;
import br.finax.dto.InvoiceValues;
import br.finax.models.InvoicePayment;
import br.finax.repository.*;
import br.finax.utils.UtilsService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static br.finax.utils.UtilsService.compressImage;
import static br.finax.utils.UtilsService.compressPdf;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoicePaymentRepository invoicePaymentRepository;
    private final CashFlowRepository cashFlowRepository;

    private final AccountsRepository accountsRepository;
    private final CategoryRepository categoryRepository;
    private final CreditCardRepository creditCardRepository;

    private final UtilsService utilsService;

    public InvoiceMonthValues getInvoiceAndReleases(long creditCardId, String selectedMonth) {
        final Optional<Invoice> invoice =
                invoiceRepository.findByMonthYear(utilsService.getAuthUser().getId(), creditCardId, selectedMonth);

        return invoice.map(value -> new InvoiceMonthValues(
                value,
                invoicePaymentRepository.findAllByInvoiceIdOrderByPaymentDateDescPaymentHourDesc(value.getId()),
                cashFlowRepository.getByInvoice(value.getId())
        )).orElseGet(() -> new InvoiceMonthValues(
                null,
                new ArrayList<>(),
                new ArrayList<>()
        ));
    }

    public InvoiceValues getValues(long creditCardId) {
        final long userId = utilsService.getAuthUser().getId();

        final CreditCard creditCard = creditCardRepository.findById(creditCardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

        return new InvoiceValues(
                creditCard,
                accountsRepository.getBasicList(userId),
                categoryRepository.findByUser(userId),
                creditCardRepository.getBasicList(userId)
        );
    }

    public InvoicePayment savePayment(InvoicePayment payment) {
        try {
            return invoicePaymentRepository.save(payment);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error saving invoice payment");
        }
    }

    public InvoicePayment saveInvoiceAttachment(long invoiceId, MultipartFile attachment) {
        try {
            if (attachment == null || attachment.isEmpty())
                throw new RuntimeException("Invalid attachment");

            final InvoicePayment payment = invoicePaymentRepository.findById(invoiceId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invoice not found"));

            final String fileExtension = Objects.requireNonNull(attachment.getOriginalFilename()).split("\\.")[1];

            payment.setAttachmentName(attachment.getOriginalFilename());

            switch (fileExtension) {
                case "pdf":
                    payment.setAttachment(compressPdf(attachment.getBytes()));
                    break;
                case "png", "webp":
                    payment.setAttachment(attachment.getBytes());
                    break;
                default:
                    payment.setAttachment(compressImage(attachment.getBytes(), true));
            }

            return invoicePaymentRepository.save(payment);
        } catch (RuntimeException | IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public InvoicePayment removeAttachment(long invoiceId) {
        try {
            final InvoicePayment payment = invoicePaymentRepository.findById(invoiceId)
                    .orElseThrow(() -> new RuntimeException("Invoice not found"));

            payment.setAttachment(null);
            payment.setAttachmentName(null);

            return invoicePaymentRepository.save(payment);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
