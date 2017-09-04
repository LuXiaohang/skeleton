package dao;

import api.TagResponse;
import generated.tables.records.TagsRecord;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static generated.Tables.*;

public class TagDao {
    DSLContext dsl;

    public TagDao(Configuration jooqConfig) {
        this.dsl = DSL.using(jooqConfig);
    }

    public int insert(String tag) {
        TagsRecord tagsRecord = dsl
                .insertInto(TAGS, TAGS.TAG)
                .values(tag)
                .returning(TAGS.ID)
                .fetchOne();

        checkState(tagsRecord != null && tagsRecord.getId() != null, "Insert failed");

        return tagsRecord.getId();
    }

    public void insertTagReceipt(int tagid, int receiptid){
        System.out.println("I am in insertTagReceipt");
        System.out.println(tagid);
        System.out.println(receiptid);
        System.out.println(dsl.insertInto(TAGRECEIPTS,TAGRECEIPTS.TAGID,TAGRECEIPTS.RECEIPTSID).values(tagid,receiptid));
        dsl.insertInto(TAGRECEIPTS,TAGRECEIPTS.TAGID,TAGRECEIPTS.RECEIPTSID)
                .values(tagid,receiptid)
                .returning(TAGS.ID)
                .fetchOne();;
    }

    public boolean tagExists(String tag){
        System.out.println("I am in tagExist");
        //System.out.println(dsl.fetchExists(TAGS,TAGS.TAG.eq(tag)));
        return dsl.fetchExists(TAGS,TAGS.TAG.eq(tag));
    }

    public int getTagid(String tag){
        return dsl.selectFrom(TAGS).where(TAGS.TAG.eq(tag)).fetchOne().getId();
    }

    public boolean tagreceiptExists(int tagid, int receiptid){
        System.out.println("I am in tagreceiptExist");
        System.out.println(dsl.fetchExists(TAGRECEIPTS,TAGRECEIPTS.TAGID.eq(tagid).and(TAGRECEIPTS.RECEIPTSID.eq(receiptid))));
        return dsl.fetchExists(TAGRECEIPTS,TAGRECEIPTS.TAGID.eq(tagid).and(TAGRECEIPTS.RECEIPTSID.eq(receiptid)));
    }

    public void delete(int tagid, int receiptid){
        System.out.println("I am in tagreceiptsdelete");
        dsl.delete(TAGRECEIPTS)
                .where(TAGRECEIPTS.TAGID.eq(tagid))
                .and(TAGRECEIPTS.RECEIPTSID.eq(receiptid))
                .execute();
    }

    /*public void insertsomedata(){
        dsl.insertInto(RECEIPTS,RECEIPTS.MERCHANT,RECEIPTS.AMOUNT).values("Ice cream",new java.math.BigDecimal(22.5));
        dsl.insertInto(RECEIPTS,RECEIPTS.MERCHANT,RECEIPTS.AMOUNT).values("Cake",new java.math.BigDecimal(23.5));
        dsl.insertInto(TAGS,TAGS.TAG).values("Ice");
        dsl.insertInto(TAGS,TAGS.TAG).values("Sweet");
        dsl.insertInto(TAGS,TAGS.TAG).values("Round");
        dsl.insertInto(TAGRECEIPTS,TAGRECEIPTS.RECEIPTSID,TAGRECEIPTS.TAGID).values(1,1);
        dsl.insertInto(TAGRECEIPTS,TAGRECEIPTS.RECEIPTSID,TAGRECEIPTS.TAGID).values(1,2);
        dsl.insertInto(TAGRECEIPTS,TAGRECEIPTS.RECEIPTSID,TAGRECEIPTS.TAGID).values(2,2);
        dsl.insertInto(TAGRECEIPTS,TAGRECEIPTS.RECEIPTSID,TAGRECEIPTS.TAGID).values(2,3);

    }*/


}
