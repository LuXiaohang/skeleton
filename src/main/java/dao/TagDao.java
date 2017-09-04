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
        dsl.insertInto(TAGRECEIPTS,TAGRECEIPTS.TAGID,TAGRECEIPTS.RECEIPTSID).values(tagid,receiptid);
    }

    public boolean tagExists(String tag){
        return dsl.fetchExists(TAGS,TAGS.TAG.eq(tag));
    }

    public int getTagid(String tag){
        return dsl.selectFrom(TAGS).where(TAGS.TAG.eq(tag)).fetchOne().getId();
    }

    public boolean tagreceiptExists(int tagid, int receiptid){
        return dsl.fetchExists(TAGRECEIPTS,TAGRECEIPTS.TAGID.eq(tagid).and(TAGRECEIPTS.RECEIPTSID.eq(receiptid)));
    }

    public void delete(int tagid, int receiptid){
        dsl.delete(TAGRECEIPTS)
                .where(TAGRECEIPTS.TAGID.eq(tagid))
                .and(TAGRECEIPTS.RECEIPTSID.eq(receiptid))
                .execute();
    }


}
