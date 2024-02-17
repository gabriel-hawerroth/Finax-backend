package br.finax.utils;

import java.util.Date;

@SuppressWarnings("unused")
public class InterfacesSQL {

    public interface GenericIdDs {
        Long getId();
        String getDs();
    }

    public interface MonthlyReleases {
        Long getId();
        Long getUserId();
        String getDescription();
        Long getAccountId();
        String getAccountName();
        Double getAmount();
        String getType();
        Boolean getDone();
        Long getTargetAccountId();
        String getTargetAccountName();
        Long getCategoryId();
        String getCategoryName();
        String getCategoryColor();
        String getCategoryIcon();
        Date getDate();
        String getTime();
        String getObservation();
        String getAttachmentName();
        Long getDuplicatedReleaseId();
        Boolean getIsDuplicatedRelease();
    }

    public interface MonthlyBalance {
        Double getRevenues();
        Double getExpenses();
        Double getBalance();
        Double getGeneralBalance();
        Double getExpectedBalance();
    }

    public interface HomeBalances {
        Double getRevenues();
        Double getExpenses();
    }

    public interface UserCreditCards {
        Long getId();
        Long getUser_id();
        String getName();
        Double getCard_limit();
        int getClose_day();
        int getExpires_day();
        String getImage();
        Long getStandard_payment_account_id();
        boolean getActive();
        String getAccount_name();
        String getAccount_image();
    }

    public interface AccountBasicList {
        Long getId();
        String getName();
        String getImage();
    }

    public interface CardBasicList {
        Long getId();
        String getName();
        String getImage();
    }
}