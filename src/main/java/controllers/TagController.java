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
import java.util.ArrayList;
import javax.ws.rs.core.Response;

import static java.util.stream.Collectors.toList;

@Path("/tags/{tag}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TagController {
    final ReceiptDao receipts;
    final TagDao tags;



    public TagController(TagDao tags, ReceiptDao receipts) {
        this.tags = tags;
        this.receipts = receipts;
    }

    @PUT
    public String toggleTag(@PathParam("tag") String tag,int receiptid) {
        System.out.println(tag);
        System.out.println(receiptid);
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
            System.out.println("tagid");
            System.out.println(tagid);
            tags.insertTagReceipt(tagid,receiptid);
            return "tag";
        }

    }

    @GET
    public List<ReceiptResponse> getTagReceipts(@NotNull @PathParam("tag") String tag) {
        System.out.println(tag);
        if(!tags.tagExists(tag)){new WebApplicationException("tag does not exist", Response.Status.NOT_FOUND);}
        int tagid=tags.getTagid(tag);
        List<Integer> receiptIDs = receipts.getReceiptIdByTagid(tagid);

        // search through the receipt table to retrieve all the receipt with certain tag id
        List<ReceiptsRecord> ReceiptRecords = new ArrayList<ReceiptsRecord>();

        for (int id: receiptIDs) {
            ReceiptRecords.add(receipts.getReceiptFromID(id));
        }

        return ReceiptRecords.stream().map(ReceiptResponse::new).collect(toList());
    }




}
