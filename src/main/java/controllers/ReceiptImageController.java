package controllers;

import api.ReceiptSuggestionResponse;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.Collections;
import java.util.regex.*;
import java.util.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import org.hibernate.validator.constraints.NotEmpty;

import static java.lang.System.out;

@Path("/images")
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.APPLICATION_JSON)
public class ReceiptImageController {
    private final AnnotateImageRequest.Builder requestBuilder;

    public ReceiptImageController() {
        // DOCUMENT_TEXT_DETECTION is not the best or only OCR method available
        Feature ocrFeature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        this.requestBuilder = AnnotateImageRequest.newBuilder().addFeatures(ocrFeature);

    }

    public static boolean isNumeric(String str){
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    @POST
    public ReceiptSuggestionResponse parseReceipt(@NotEmpty String base64EncodedImage) throws Exception {
        base64EncodedImage = base64EncodedImage.replaceAll("\\s+","");
        Image img = Image.newBuilder().setContent(ByteString.copyFrom(Base64.getDecoder().decode(base64EncodedImage))).build();
        AnnotateImageRequest request = this.requestBuilder.setImage(img).build();

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse responses = client.batchAnnotateImages(Collections.singletonList(request));
            AnnotateImageResponse res = responses.getResponses(0);

            String merchantName = null;
            BigDecimal amount = null;
            for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                String name=annotation.getDescription();
                if(!ReceiptImageController.isNumeric(name)){
                    merchantName=name;
                    break;
                }

            }
            for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                String name=annotation.getDescription();
                System.out.println(name);
                if(ReceiptImageController.isNumeric(name)){
                    amount = new BigDecimal(name);
                }

            }


            /* Map<String,Integer> NameCount = new HashMap<String,Integer>();
            int i = 0;
            // Your Algo Here!!
            // Sort text annotations by bounding polygon.  Top-most non-decimal text is the merchant
            // bottom-most decimal text is the total amount
            for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                i++;
                if(i==1){
                    continue;
                }
                String name = annotation.getDescription();
                if(!NameCount.containsKey(name)){
                    NameCount.put(name,1);
                }
                else{
                    Integer c = NameCount.get(name);
                    NameCount.put(name,c+1);
                }
                //out.printf("Position : %s\n", annotation.getBoundingPoly().getVertices(0));
                //out.printf("Text: %s\n", annotation.getDescription());
            }

            for (Map.Entry<String, Integer> entry : NameCount.entrySet()) {

                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

            }
            // I define the top most is the first half of the annotation
            // I define the bottom most is the second half of the annotation
            i = 0;
            int namesize = NameCount.size();
            out.println("namesize:"+namesize);
            int merchantvalue=0;
            int amountvalue=0;
            for (Map.Entry<String, Integer> entry : NameCount.entrySet()){
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                i=i+1;
                if(i<namesize/2+1){
                    if(entry.getValue()>merchantvalue && !ReceiptImageController.isNumeric(entry.getKey())){
                        merchantvalue = entry.getValue();
                        merchantName = entry.getKey();
                    }
                }
                else{
                    if(entry.getValue()>=amountvalue && ReceiptImageController.isNumeric(entry.getKey())){
                        amountvalue = entry.getValue();
                        amount = new BigDecimal(entry.getKey());
                    }
                }
            }
            System.out.println(merchantName);
            System.out.println(amount);*/
            //TextAnnotation fullTextAnnotation = res.getFullTextAnnotation();
            return new ReceiptSuggestionResponse(merchantName, amount);
        }
    }
}