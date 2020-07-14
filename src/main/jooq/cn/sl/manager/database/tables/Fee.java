/*
 * This file is generated by jOOQ.
 */
package cn.sl.manager.database.tables;


import cn.sl.manager.database.Sl;
import cn.sl.manager.database.tables.records.FeeRecord;

import java.math.BigDecimal;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row2;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Fee extends TableImpl<FeeRecord> {

    private static final long serialVersionUID = 1409512169;

    /**
     * The reference instance of <code>sl.fee</code>
     */
    public static final Fee FEE = new Fee();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<FeeRecord> getRecordType() {
        return FeeRecord.class;
    }

    /**
     * The column <code>sl.fee.fee</code>.
     */
    public final TableField<FeeRecord, BigDecimal> FEE_ = createField(DSL.name("fee"), org.jooq.impl.SQLDataType.DECIMAL(10, 2).nullable(false), this, "");

    /**
     * The column <code>sl.fee.code</code>.
     */
    public final TableField<FeeRecord, String> CODE = createField(DSL.name("code"), org.jooq.impl.SQLDataType.VARCHAR(20).nullable(false), this, "");

    /**
     * Create a <code>sl.fee</code> table reference
     */
    public Fee() {
        this(DSL.name("fee"), null);
    }

    /**
     * Create an aliased <code>sl.fee</code> table reference
     */
    public Fee(String alias) {
        this(DSL.name(alias), FEE);
    }

    /**
     * Create an aliased <code>sl.fee</code> table reference
     */
    public Fee(Name alias) {
        this(alias, FEE);
    }

    private Fee(Name alias, Table<FeeRecord> aliased) {
        this(alias, aliased, null);
    }

    private Fee(Name alias, Table<FeeRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> Fee(Table<O> child, ForeignKey<O, FeeRecord> key) {
        super(child, key, FEE);
    }

    @Override
    public Schema getSchema() {
        return Sl.SL;
    }

    @Override
    public Fee as(String alias) {
        return new Fee(DSL.name(alias), this);
    }

    @Override
    public Fee as(Name alias) {
        return new Fee(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Fee rename(String name) {
        return new Fee(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Fee rename(Name name) {
        return new Fee(name, null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<BigDecimal, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }
}