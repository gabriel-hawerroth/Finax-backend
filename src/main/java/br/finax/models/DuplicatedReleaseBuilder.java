package br.finax.models;

import java.time.LocalDate;

public class DuplicatedReleaseBuilder {
    private final CashFlow duplicatedRelease;

    public DuplicatedReleaseBuilder(CashFlow original) {
        // Copies the relevant properties from the original to the duplicate
        this.duplicatedRelease = new CashFlow();
        duplicatedRelease.setUserId(original.getUserId());
        duplicatedRelease.setDescription(original.getDescription());
        duplicatedRelease.setAccountId(original.getAccountId());
        duplicatedRelease.setAmount(0.0);
        duplicatedRelease.setType(original.getType());
        duplicatedRelease.setDone(false);
        duplicatedRelease.setTargetAccountId(original.getTargetAccountId());
        duplicatedRelease.setCategoryId(original.getCategoryId());
        duplicatedRelease.setDate(null);
        duplicatedRelease.setTime(original.getTime());
        duplicatedRelease.setObservation(original.getObservation());
        duplicatedRelease.setAttachment(null);
        duplicatedRelease.setAttachmentName(null);
        duplicatedRelease.setDuplicatedReleaseId(original.getId());
        duplicatedRelease.setRepeat(original.getRepeat());
        duplicatedRelease.setFixedBy(original.getFixedBy());
        duplicatedRelease.setInvoice_id(original.getInvoice_id());
    }

    public DuplicatedReleaseBuilder amount(Double amount) {
        duplicatedRelease.setAmount(amount);
        return this;
    }

    public DuplicatedReleaseBuilder date(LocalDate date) {
        duplicatedRelease.setDate(date);
        return this;
    }

    public CashFlow build() {
        return duplicatedRelease;
    }
}