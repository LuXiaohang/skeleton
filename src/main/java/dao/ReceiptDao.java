package dao;

import api.ReceiptResponse;
import generated.tables.Tagreceipts;
import generated.tables.records.ReceiptsRecord;
import generated.tables.records.TagsRecord;
import generated.tables.records.TagreceiptsRecord;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import dao.TagDao;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static generated.Tables.RECEIPTS;
import static generated.Tables.TAGS;
import static generated.Tables.TAGRECEIPTS;

public class ReceiptDao {
    DSLContext dsl;
    TagDao tags;

    public ReceiptDao(Configuration jooqConfig) {
        this.dsl = DSL.using(jooqConfig);
    }

    public int insert(String merchantName, BigDecimal amount) {
        ReceiptsRecord receiptsRecord = dsl
                .insertInto(RECEIPTS, RECEIPTS.MERCHANT, RECEIPTS.AMOUNT)
                .values(merchantName, amount)
                .returning(RECEIPTS.ID)
                .fetchOne();

        checkState(receiptsRecord != null && receiptsRecord.getId() != null, "Insert failed");

        return receiptsRecord.getId();
    }

    public List<ReceiptsRecord> getAllReceipts() {
        return dsl.selectFrom(RECEIPTS).fetch();
    }

// return all the receipts defined by a tag
    public List<ReceiptsRecord> getAllTagReceipts(String tag){
        int tagid = dsl.selectFrom(TAGS).where(TAGS.TAG.eq(tag)).fetchOne().getId();
        return dsl.selectFrom(RECEIPTS).fetch();
                //dsl.select().from(RECEIPTS).join(TAGRECEIPTS).on(RECEIPTS.ID.eq(TAGRECEIPTS.RECEIPTSID)).where(TAGRECEIPTS.TAGID.eq(tagid)).fetch();

    }
// indentify if a receipt id exists
    public boolean idExists(int receiptId){
        return dsl.fetchExists(RECEIPTS,RECEIPTS.ID.eq(receiptId));
    }
}
