package controllers;

import api.CreateTagRequest;
import api.TagResponse;
import api.ReceiptResponse;
import dao.TagDao;
import dao.ReceiptDao;
import generated.tables.records.TagsRecord;
import generated.tables.records.ReceiptsRecord;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import javax.ws.rs.core.Response;

import static java.util.stream.Collectors.toList;

@Path("/tags/{tag}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TagController {
    ReceiptDao receipts;
    final TagDao tags;


    public TagController(TagDao tags) {
        this.tags = tags;
    }

    @PUT
    public String toggleTag(@PathParam("tag") String tag,int receiptid) {
        if (!receipts.idExists(receiptid)) {
            return "no such receiptid";
        }

        // find or create tag by tagname and receiptid
        // first find if exists this tag, if yes, find if there is link to the receipt id, if yes,delete;no,create
        // if no, create this tag, and link this tag to receiptid
        if (tags.tagExists(tag)){
            int tagid = tags.getTagid(tag);
            if(tags.tagreceiptExists(tagid,receiptid)){
                tags.delete(tagid,receiptid);
                return "untag";
            }
            else {
                tags.insertTagReceipt(tagid,receiptid);
                return "tag";
            }
        }
        else {
            int tagid = tags.insert(tag);
            tags.insertTagReceipt(tagid,receiptid);
            return "tag";
        }

    }

    @GET
    public List<ReceiptResponse> getTagReceipts(String tag) {
        List<ReceiptsRecord> receiptRecords = receipts.getAllTagReceipts(tag);
        return receiptRecords.stream().map(ReceiptResponse::new).collect(toList());
    }




}
