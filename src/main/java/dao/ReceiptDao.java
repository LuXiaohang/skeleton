package dao;

import api.ReceiptResponse;
import generated.tables.Receipts;
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
import java.util.ArrayList;

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

    public List<ReceiptResponse> getAllReceipts() {
        List<ReceiptsRecord> records = dsl.selectFrom(RECEIPTS).fetch();
        ArrayList<ReceiptResponse> responses = new ArrayList<>();
        for (ReceiptsRecord record : records) {
            List<TagreceiptsRecord> tagreceiptsRecords = dsl.selectFrom(TAGRECEIPTS).where(TAGRECEIPTS.RECEIPTSID.eq(record.getId())).fetch();
            List<TagsRecord> tagsRecords= new ArrayList<>();
            for(TagreceiptsRecord tagreceiptsRecord:tagreceiptsRecords)
            {
                TagsRecord tagsRecord = dsl.selectFrom(TAGS).where(TAGS.ID.eq(tagreceiptsRecord.getTagid())).fetchOne();
                tagsRecords.add(tagsRecord);
            }
            ReceiptResponse response = new ReceiptResponse(record);
            response.setTags(tagsRecords);
            responses.add(response);
        }
        return responses;
    }

    // return all the receiptsid defined by a tagid
    public ReceiptsRecord getReceiptFromID(int id){
        return dsl.selectFrom(RECEIPTS).where(RECEIPTS.ID.eq(id)).fetchOne();
    }

    public List<Integer> getReceiptIdByTagid(int tagid){
        return dsl.selectFrom(TAGRECEIPTS).where(TAGRECEIPTS.TAGID.eq(tagid)).fetch(TAGRECEIPTS.RECEIPTSID);
    }





// indentify if a receipt id exists
    public boolean idExists(int receiptId){
        return dsl.fetchExists(RECEIPTS,RECEIPTS.ID.eq(receiptId));
    }
}
