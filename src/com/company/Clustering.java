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
                doc.termWeights.set(i, twidf);
                docLength += Math.pow(twidf, 2);
            }
        }
        //Nomalization
    }


    /**
     * Cluster the documents
     * For kmeans clustering, use the first and the seventh documents as the initial centroids
     */
    public void cluster() {
        //TO BE COMPLETED

    }


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
    ArrayList<Integer> termIds;
    ArrayList<Double> termWeights;
    double[] termVec;

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