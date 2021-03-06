/*
 * This file is generated by jOOQ.
 */
package cn.sl.manager.database;


import cn.sl.manager.database.tables.AccountCheckout;
import cn.sl.manager.database.tables.AccountRecord;
import cn.sl.manager.database.tables.Bank;
import cn.sl.manager.database.tables.Berth;
import cn.sl.manager.database.tables.BerthOrder;
import cn.sl.manager.database.tables.DiscountCoupon;
import cn.sl.manager.database.tables.FinanceBook;
import cn.sl.manager.database.tables.Goods;
import cn.sl.manager.database.tables.Motorcade;
import cn.sl.manager.database.tables.User;
import cn.sl.manager.database.tables.records.AccountCheckoutRecord;
import cn.sl.manager.database.tables.records.AccountRecordRecord;
import cn.sl.manager.database.tables.records.BankRecord;
import cn.sl.manager.database.tables.records.BerthOrderRecord;
import cn.sl.manager.database.tables.records.BerthRecord;
import cn.sl.manager.database.tables.records.DiscountCouponRecord;
import cn.sl.manager.database.tables.records.FinanceBookRecord;
import cn.sl.manager.database.tables.records.GoodsRecord;
import cn.sl.manager.database.tables.records.MotorcadeRecord;
import cn.sl.manager.database.tables.records.UserRecord;

import org.jooq.Identity;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code>sl</code> schema.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<AccountCheckoutRecord, Integer> IDENTITY_ACCOUNT_CHECKOUT = Identities0.IDENTITY_ACCOUNT_CHECKOUT;
    public static final Identity<AccountRecordRecord, Integer> IDENTITY_ACCOUNT_RECORD = Identities0.IDENTITY_ACCOUNT_RECORD;
    public static final Identity<BankRecord, Integer> IDENTITY_BANK = Identities0.IDENTITY_BANK;
    public static final Identity<BerthRecord, Integer> IDENTITY_BERTH = Identities0.IDENTITY_BERTH;
    public static final Identity<BerthOrderRecord, Integer> IDENTITY_BERTH_ORDER = Identities0.IDENTITY_BERTH_ORDER;
    public static final Identity<DiscountCouponRecord, Integer> IDENTITY_DISCOUNT_COUPON = Identities0.IDENTITY_DISCOUNT_COUPON;
    public static final Identity<FinanceBookRecord, Integer> IDENTITY_FINANCE_BOOK = Identities0.IDENTITY_FINANCE_BOOK;
    public static final Identity<GoodsRecord, Integer> IDENTITY_GOODS = Identities0.IDENTITY_GOODS;
    public static final Identity<MotorcadeRecord, Integer> IDENTITY_MOTORCADE = Identities0.IDENTITY_MOTORCADE;
    public static final Identity<UserRecord, Integer> IDENTITY_USER = Identities0.IDENTITY_USER;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<AccountCheckoutRecord> KEY_ACCOUNT_CHECKOUT_PRIMARY = UniqueKeys0.KEY_ACCOUNT_CHECKOUT_PRIMARY;
    public static final UniqueKey<AccountRecordRecord> KEY_ACCOUNT_RECORD_PRIMARY = UniqueKeys0.KEY_ACCOUNT_RECORD_PRIMARY;
    public static final UniqueKey<BankRecord> KEY_BANK_PRIMARY = UniqueKeys0.KEY_BANK_PRIMARY;
    public static final UniqueKey<BankRecord> KEY_BANK_UK_CODE = UniqueKeys0.KEY_BANK_UK_CODE;
    public static final UniqueKey<BerthRecord> KEY_BERTH_PRIMARY = UniqueKeys0.KEY_BERTH_PRIMARY;
    public static final UniqueKey<BerthOrderRecord> KEY_BERTH_ORDER_PRIMARY = UniqueKeys0.KEY_BERTH_ORDER_PRIMARY;
    public static final UniqueKey<DiscountCouponRecord> KEY_DISCOUNT_COUPON_PRIMARY = UniqueKeys0.KEY_DISCOUNT_COUPON_PRIMARY;
    public static final UniqueKey<FinanceBookRecord> KEY_FINANCE_BOOK_PRIMARY = UniqueKeys0.KEY_FINANCE_BOOK_PRIMARY;
    public static final UniqueKey<FinanceBookRecord> KEY_FINANCE_BOOK_UK_CODE = UniqueKeys0.KEY_FINANCE_BOOK_UK_CODE;
    public static final UniqueKey<GoodsRecord> KEY_GOODS_PRIMARY = UniqueKeys0.KEY_GOODS_PRIMARY;
    public static final UniqueKey<MotorcadeRecord> KEY_MOTORCADE_PRIMARY = UniqueKeys0.KEY_MOTORCADE_PRIMARY;
    public static final UniqueKey<UserRecord> KEY_USER_PRIMARY = UniqueKeys0.KEY_USER_PRIMARY;
    public static final UniqueKey<UserRecord> KEY_USER_USER_PHONE_UINDEX = UniqueKeys0.KEY_USER_USER_PHONE_UINDEX;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 {
        public static Identity<AccountCheckoutRecord, Integer> IDENTITY_ACCOUNT_CHECKOUT = Internal.createIdentity(AccountCheckout.ACCOUNT_CHECKOUT, AccountCheckout.ACCOUNT_CHECKOUT.ID);
        public static Identity<AccountRecordRecord, Integer> IDENTITY_ACCOUNT_RECORD = Internal.createIdentity(AccountRecord.ACCOUNT_RECORD, AccountRecord.ACCOUNT_RECORD.ID);
        public static Identity<BankRecord, Integer> IDENTITY_BANK = Internal.createIdentity(Bank.BANK, Bank.BANK.ID);
        public static Identity<BerthRecord, Integer> IDENTITY_BERTH = Internal.createIdentity(Berth.BERTH, Berth.BERTH.ID);
        public static Identity<BerthOrderRecord, Integer> IDENTITY_BERTH_ORDER = Internal.createIdentity(BerthOrder.BERTH_ORDER, BerthOrder.BERTH_ORDER.ID);
        public static Identity<DiscountCouponRecord, Integer> IDENTITY_DISCOUNT_COUPON = Internal.createIdentity(DiscountCoupon.DISCOUNT_COUPON, DiscountCoupon.DISCOUNT_COUPON.ID);
        public static Identity<FinanceBookRecord, Integer> IDENTITY_FINANCE_BOOK = Internal.createIdentity(FinanceBook.FINANCE_BOOK, FinanceBook.FINANCE_BOOK.ID);
        public static Identity<GoodsRecord, Integer> IDENTITY_GOODS = Internal.createIdentity(Goods.GOODS, Goods.GOODS.ID);
        public static Identity<MotorcadeRecord, Integer> IDENTITY_MOTORCADE = Internal.createIdentity(Motorcade.MOTORCADE, Motorcade.MOTORCADE.ID);
        public static Identity<UserRecord, Integer> IDENTITY_USER = Internal.createIdentity(User.USER, User.USER.ID);
    }

    private static class UniqueKeys0 {
        public static final UniqueKey<AccountCheckoutRecord> KEY_ACCOUNT_CHECKOUT_PRIMARY = Internal.createUniqueKey(AccountCheckout.ACCOUNT_CHECKOUT, "KEY_account_checkout_PRIMARY", new TableField[] { AccountCheckout.ACCOUNT_CHECKOUT.ID }, true);
        public static final UniqueKey<AccountRecordRecord> KEY_ACCOUNT_RECORD_PRIMARY = Internal.createUniqueKey(AccountRecord.ACCOUNT_RECORD, "KEY_account_record_PRIMARY", new TableField[] { AccountRecord.ACCOUNT_RECORD.ID }, true);
        public static final UniqueKey<BankRecord> KEY_BANK_PRIMARY = Internal.createUniqueKey(Bank.BANK, "KEY_bank_PRIMARY", new TableField[] { Bank.BANK.ID }, true);
        public static final UniqueKey<BankRecord> KEY_BANK_UK_CODE = Internal.createUniqueKey(Bank.BANK, "KEY_bank_uk_code", new TableField[] { Bank.BANK.CODE }, true);
        public static final UniqueKey<BerthRecord> KEY_BERTH_PRIMARY = Internal.createUniqueKey(Berth.BERTH, "KEY_berth_PRIMARY", new TableField[] { Berth.BERTH.ID }, true);
        public static final UniqueKey<BerthOrderRecord> KEY_BERTH_ORDER_PRIMARY = Internal.createUniqueKey(BerthOrder.BERTH_ORDER, "KEY_berth_order_PRIMARY", new TableField[] { BerthOrder.BERTH_ORDER.ID }, true);
        public static final UniqueKey<DiscountCouponRecord> KEY_DISCOUNT_COUPON_PRIMARY = Internal.createUniqueKey(DiscountCoupon.DISCOUNT_COUPON, "KEY_discount_coupon_PRIMARY", new TableField[] { DiscountCoupon.DISCOUNT_COUPON.ID }, true);
        public static final UniqueKey<FinanceBookRecord> KEY_FINANCE_BOOK_PRIMARY = Internal.createUniqueKey(FinanceBook.FINANCE_BOOK, "KEY_finance_book_PRIMARY", new TableField[] { FinanceBook.FINANCE_BOOK.ID }, true);
        public static final UniqueKey<FinanceBookRecord> KEY_FINANCE_BOOK_UK_CODE = Internal.createUniqueKey(FinanceBook.FINANCE_BOOK, "KEY_finance_book_uk_code", new TableField[] { FinanceBook.FINANCE_BOOK.CODE }, true);
        public static final UniqueKey<GoodsRecord> KEY_GOODS_PRIMARY = Internal.createUniqueKey(Goods.GOODS, "KEY_goods_PRIMARY", new TableField[] { Goods.GOODS.ID }, true);
        public static final UniqueKey<MotorcadeRecord> KEY_MOTORCADE_PRIMARY = Internal.createUniqueKey(Motorcade.MOTORCADE, "KEY_motorcade_PRIMARY", new TableField[] { Motorcade.MOTORCADE.ID }, true);
        public static final UniqueKey<UserRecord> KEY_USER_PRIMARY = Internal.createUniqueKey(User.USER, "KEY_user_PRIMARY", new TableField[] { User.USER.ID }, true);
        public static final UniqueKey<UserRecord> KEY_USER_USER_PHONE_UINDEX = Internal.createUniqueKey(User.USER, "KEY_user_user_phone_uindex", new TableField[] { User.USER.PHONE }, true);
    }
}
