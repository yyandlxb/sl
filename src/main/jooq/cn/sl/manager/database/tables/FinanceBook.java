/*
 * This file is generated by jOOQ.
 */
package cn.sl.manager.database.tables;


import cn.sl.manager.database.Keys;
import cn.sl.manager.database.Sl;
import cn.sl.manager.database.tables.records.FinanceBookRecord;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row12;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class FinanceBook extends TableImpl<FinanceBookRecord> {

    private static final long serialVersionUID = 973539439;

    /**
     * The reference instance of <code>sl.finance_book</code>
     */
    public static final FinanceBook FINANCE_BOOK = new FinanceBook();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<FinanceBookRecord> getRecordType() {
        return FinanceBookRecord.class;
    }

    /**
     * The column <code>sl.finance_book.id</code>.
     */
    public final TableField<FinanceBookRecord, Integer> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>sl.finance_book.code</code>.
     */
    public final TableField<FinanceBookRecord, String> CODE = createField(DSL.name("code"), org.jooq.impl.SQLDataType.VARCHAR(32).nullable(false), this, "");

    /**
     * The column <code>sl.finance_book.created_at</code>.
     */
    public final TableField<FinanceBookRecord, LocalDateTime> CREATED_AT = createField(DSL.name("created_at"), org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>sl.finance_book.updated_at</code>.
     */
    public final TableField<FinanceBookRecord, LocalDateTime> UPDATED_AT = createField(DSL.name("updated_at"), org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>sl.finance_book.user_id</code>.
     */
    public final TableField<FinanceBookRecord, Integer> USER_ID = createField(DSL.name("user_id"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>sl.finance_book.company_id</code>.
     */
    public final TableField<FinanceBookRecord, Integer> COMPANY_ID = createField(DSL.name("company_id"), org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>sl.finance_book.goods_id</code>.
     */
    public final TableField<FinanceBookRecord, Integer> GOODS_ID = createField(DSL.name("goods_id"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>sl.finance_book.gross_weight</code>.
     */
    public final TableField<FinanceBookRecord, BigDecimal> GROSS_WEIGHT = createField(DSL.name("gross_weight"), org.jooq.impl.SQLDataType.DECIMAL(10, 2).nullable(false), this, "");

    /**
     * The column <code>sl.finance_book.tare_weight</code>.
     */
    public final TableField<FinanceBookRecord, BigDecimal> TARE_WEIGHT = createField(DSL.name("tare_weight"), org.jooq.impl.SQLDataType.DECIMAL(10, 2).nullable(false), this, "");

    /**
     * The column <code>sl.finance_book.fee</code>.
     */
    public final TableField<FinanceBookRecord, BigDecimal> FEE = createField(DSL.name("fee"), org.jooq.impl.SQLDataType.DECIMAL(10, 2), this, "");

    /**
     * The column <code>sl.finance_book.status</code>.
     */
    public final TableField<FinanceBookRecord, Byte> STATUS = createField(DSL.name("status"), org.jooq.impl.SQLDataType.TINYINT, this, "");

    /**
     * The column <code>sl.finance_book.send_wight</code>.
     */
    public final TableField<FinanceBookRecord, BigDecimal> SEND_WIGHT = createField(DSL.name("send_wight"), org.jooq.impl.SQLDataType.DECIMAL(10, 2).defaultValue(org.jooq.impl.DSL.inline("0.00", org.jooq.impl.SQLDataType.DECIMAL)), this, "");

    /**
     * Create a <code>sl.finance_book</code> table reference
     */
    public FinanceBook() {
        this(DSL.name("finance_book"), null);
    }

    /**
     * Create an aliased <code>sl.finance_book</code> table reference
     */
    public FinanceBook(String alias) {
        this(DSL.name(alias), FINANCE_BOOK);
    }

    /**
     * Create an aliased <code>sl.finance_book</code> table reference
     */
    public FinanceBook(Name alias) {
        this(alias, FINANCE_BOOK);
    }

    private FinanceBook(Name alias, Table<FinanceBookRecord> aliased) {
        this(alias, aliased, null);
    }

    private FinanceBook(Name alias, Table<FinanceBookRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> FinanceBook(Table<O> child, ForeignKey<O, FinanceBookRecord> key) {
        super(child, key, FINANCE_BOOK);
    }

    @Override
    public Schema getSchema() {
        return Sl.SL;
    }

    @Override
    public Identity<FinanceBookRecord, Integer> getIdentity() {
        return Keys.IDENTITY_FINANCE_BOOK;
    }

    @Override
    public UniqueKey<FinanceBookRecord> getPrimaryKey() {
        return Keys.KEY_FINANCE_BOOK_PRIMARY;
    }

    @Override
    public List<UniqueKey<FinanceBookRecord>> getKeys() {
        return Arrays.<UniqueKey<FinanceBookRecord>>asList(Keys.KEY_FINANCE_BOOK_PRIMARY, Keys.KEY_FINANCE_BOOK_UK_CODE);
    }

    @Override
    public FinanceBook as(String alias) {
        return new FinanceBook(DSL.name(alias), this);
    }

    @Override
    public FinanceBook as(Name alias) {
        return new FinanceBook(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public FinanceBook rename(String name) {
        return new FinanceBook(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public FinanceBook rename(Name name) {
        return new FinanceBook(name, null);
    }

    // -------------------------------------------------------------------------
    // Row12 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row12<Integer, String, LocalDateTime, LocalDateTime, Integer, Integer, Integer, BigDecimal, BigDecimal, BigDecimal, Byte, BigDecimal> fieldsRow() {
        return (Row12) super.fieldsRow();
    }
}
