package com.company;

import java.util.*;


/**
 * ISTE-612 Lab 5
 * Document clustering
 * Ji Woong Kim
 */
public class Clustering {

    int numDocs;
    int numClusters;
    int vSize;
    Doc[] docList;
    HashMap<String, Integer> termIdMap;

    ArrayList<Doc>[] clusters;
    Doc[] centroids;

    public Clustering(int numC) {
        numClusters = numC;
        clusters = new ArrayList[numClusters];
        centroids = new Doc[numClusters];
        termIdMap = new HashMap<String, Integer>();
    }


    /**
     * Load the documents to build the vector representations
     *
     * @param docs
     */
    public void preprocess(String[] docs) {

        for(int i = 0 ; i < numClusters ; i++){
            clusters[i] = new ArrayList<>();
        }
        //TO BE COMPLETED
        numDocs = docs.length;
        docList = new Doc[numDocs];
        int termId = 0;
        int docId = 0;

        for (String doc : docs) {
            String[] tokens = doc.split(" ");
            Doc docObj = new Doc(docId);
            for (String token : tokens) {
                if (!termIdMap.containsKey(token)) {
                    termIdMap.put(token, termId);
                    docObj.termIds.add(termId);
                    docObj.termWeights.add(1.0);
                    termId++;
                } else {
                    Integer tid = termIdMap.get(token);
                    int index = docObj.termIds.indexOf(tid);
                    if (index > 0) {
                        double tw = docObj.termWeights.get(index);
                        docObj.termWeights.add(index, tw++);
                    } else {
                        docObj.termIds.add(termIdMap.get(token));
                        docObj.termWeights.add(1.0);
                    }
                }
            }
            docList[docId] = docObj;
            docId++;
        }

        vSize = termId;
        //compute the tw.idf
        for (Doc doc : docList) {
            double docLength = 0;
            double[] termVec = new double[vSize];
            for (int i = 0; i < doc.termIds.size(); i++) {
                Integer tid = doc.termIds.get(i);
                double twidf = (1 + Math.log(doc.termWeights.get(i)));
                doc.termWeights.set(i, twidf);      // tfidf table filled
                docLength += Math.pow(twidf, 2);    // |d| is given
                termVec[tid] = twidf;
            }
            doc.setTermVec(termVec);
            doc.docLength = Math.sqrt(docLength);
        }



        System.out.println(docList.length);

        for (Doc doc : docList) {
            System.out.println(doc.termIds);
        }

        //assign document 1 and 7 as centroids.
        centroids[0] = docList[0];
        centroids[1] = docList[6];

        //Nomalization
    }


    /**
     * Cluster the documents
     * For kmeans clustering, use the first and the seventh documents as the initial centroids
     */
    public void cluster() {
        //TO BE COMPLETED

        for (int i = 0 ; i < docList.length ; i++ ) {
            double distToCentroid1 = calcDistance(docList[i], centroids[0]);
            double distToCentroid2 = calcDistance(docList[i], centroids[1]);
            if (distToCentroid1 > distToCentroid2) {
                clusters[0].add(docList[i]);
            }else{
                clusters[1].add(docList[i]);
            }
        }


        System.out.println("Cluster 0: " + clusters[0]);
        System.out.println("Cluster 1: " + clusters[1]);


        System.out.println("Updated Centroid");
        System.out.println(calcCentroid( clusters[0] ));
        System.out.println(calcCentroid( clusters[1] ));



    }


    /**
     * Calculate distance between two documenets
     * For kmeans clustering, calculate the distance between two documents
     */
    public Doc calcCentroid(  ArrayList<Doc> cluster ) {

        double[] vectorGross = new double[vSize];
        for(Doc doc : cluster){
            for(int j = 0 ;  j < vSize; j++){
                vectorGross[j] += doc.termVec[j];
            }
        }

        for(int j = 0 ;  j < vSize; j++) vectorGross[j] = vectorGross[j]/vSize;

        Doc centroid = new Doc();
        centroid.termVec = vectorGross;

        return centroid;
    }


    /**
     * Calculate distance between two documenets
     * For kmeans clustering, calculate the distance between two documents
     */
    public double calcDistance(Doc doc1, Doc doc2) {

        double vectorDistance = 0.0;

        for (int i = 0; i < doc1.termVec.length; i++) {
                double doc1tf = doc1.termVec[i];
                double doc2tf = doc2.termVec[i];
                vectorDistance = vectorDistance + Math.abs(doc1tf - doc2tf);
        }

        return vectorDistance;
    }


//    public void rankSearch(String[] query) {
//        //To be completed
//        //System.out.println(docs);
//        //#6
//        HashMap<Integer, Double> docs = new HashMap<Integer, Double>();
//
//        ArrayList<Doc> docList;
//        double sc;
//        for (String term : query) {
//            int index = termList.indexOf(term);
//            if (index < 0) continue;
//            docList = docLists.get(index);
//            double qtfidf = (1 + Math.log10(1)) * Math.log10(myDocs.length * 1.0) / docList.size();
//
//            //Modified
////            double qtfidf = 1+Math.log10(1);
//
//            Doc doc;
//            //Normalize the vectors
//
//            //double score = 0;
//            for (int i = 0; i < docList.size(); i++) {
//                doc = docList.get(i);
//                double score = doc.tw * qtfidf;
//
//
//                if (!docs.containsKey(doc.docId)) {
//                    docs.put(doc.docId, score);
//                } else {
//                    score += docs.get(doc.docId);
//                    docs.put(doc.docId, score);
//                }
//            }
//        }
//        System.out.println(docs);
//    }


    public static void main(String[] args) {
        String[] docs = {"hot chocolate cocoa beans",
                "cocoa ghana africa",
                "beans harvest ghana",
                "cocoa butter",
                "butter truffles",
                "sweet chocolates can",
                "brazil sweet sugar can",
                "suger can brazil",
                "sweet cake icing",
                "cake black forest"
        };
        Clustering c = new Clustering(2);
        c.preprocess(docs);
        System.out.println("Vector space representation:");
        for (int i = 0; i < c.docList.length; i++) {
            System.out.println(c.docList[i]);
        }

        c.cluster();
    }
}

/**
 * Document class for the vector representation of a document
 */
class Doc {
    int docId;
    ArrayList<Integer> termIds;     //
    ArrayList<Double> termWeights;  // In lab 5, it means that tfidf
    double[] termVec;

    double docLength;

    public Doc() {

    }

    public Doc(int id) {
        docId = id;
        termIds = new ArrayList<Integer>();
        termWeights = new ArrayList<Double>();
    }

    public void setTermVec(double[] vec) {
        termVec = vec;
    }

    public String toString() {
        String docString = "[";
        for (int i = 0; i < termVec.length; i++) {
            docString += termVec[i] + ",";
        }
        return docString + "]";
    }
}