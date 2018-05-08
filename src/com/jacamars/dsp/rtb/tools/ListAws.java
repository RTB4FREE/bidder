package com.jacamars.dsp.rtb.tools;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;

import java.util.List;

/**
 * Created by ben on 7/17/17.
 */
public class ListAws {

    static String accessKey = "";
    static String secretAccessKey = "";
    static String s3_bucket = "rtb-bidder-lists";
    public static void main(String [] args) throws Exception {

        BasicAWSCredentials creds = new BasicAWSCredentials(accessKey, secretAccessKey);
        AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(creds)).build();

        ObjectListing listing = s3.listObjects(new ListObjectsRequest().withBucketName(s3_bucket));

        processDirectory(s3, listing, s3_bucket);
    }

    public static void processDirectory(AmazonS3 s3, ObjectListing listing, String bucket) throws Exception {
        for (S3ObjectSummary objectSummary : listing.getObjectSummaries()) {
            long size = objectSummary.getSize();
            S3Object object = s3.getObject(new GetObjectRequest(bucket, objectSummary.getKey()));

            String bucketName = object.getBucketName();
            String keyName = object.getKey();

            GetObjectTaggingRequest request = new GetObjectTaggingRequest(bucketName, keyName);
            GetObjectTaggingResult result = s3.getObjectTagging(request);
            List<Tag> tags = result.getTagSet();
            String type = null;
            String name = null;

            if (tags.isEmpty()) {
                System.err.println("Error: " + keyName + " has no tags");
            } else {
                for (Tag tag : tags) {
                    String key = tag.getKey();
                    String value = tag.getValue();

                    if (key.equals("type")) {
                        type = value;
                    }

                    if (key.equals("name")) {
                        name = value;
                    }
                }

                if (name == null)
                    throw new Exception("Error: " + keyName + " is missing a name tag");
                if (name.contains(" "))
                    throw new Exception("Error: " + keyName + " has a name attribute with a space in it");
                if (type == null)
                    throw new Exception("Error: " + keyName + " has no type tag");

                if (!name.startsWith("$"))
                    name = "$" + name;

                System.out.println("type: " + type + ", name: " + name + ", size: " + size);
            }
        }
    }
}
