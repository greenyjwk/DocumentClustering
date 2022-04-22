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
        for (int i = 0; i < numClusters; i++) clusters[i] = new ArrayList<>();

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
        //compute the tw
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

        for (Doc doc : docList) System.out.println(doc.termIds);

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

        System.out.println("\n\n\nClustering: ");

        boolean iterationCheck1 = true;
        boolean iterationCheck2 = true;
        int iteration = 1;
        while (iterationCheck1 || iterationCheck2) {

            System.out.println("Iteration " + iteration);
            System.out.println("Centroid 0 " + centroids[0]);
            System.out.println("Centroid 1 " + centroids[1]);
            System.out.println("\n\n");

            clusters[0] = new ArrayList();
            clusters[1] = new ArrayList();
            double distToCentroid0 = 0.0;
            double distToCentroid1 = 0.0;
            for (int i = 0; i < docList.length; i++) {
                distToCentroid0 = calcDistance(docList[i], centroids[0]);
                distToCentroid1 = calcDistance(docList[i], centroids[1]);
                if (distToCentroid0 < distToCentroid1) clusters[0].add(docList[i]);
                else if (distToCentroid0 > distToCentroid1) clusters[1].add(docList[i]);
            }

            // previous centroids
            double[] prevCentroids0 = centroids[0].termVec;
            double[] prevCentroids1 = centroids[1].termVec;

            // updated centroids
            centroids[0] = calcCentroid(clusters[0]);
            centroids[1] = calcCentroid(clusters[1]);

            iterationCheck1 = compareCentroids(prevCentroids0, centroids[0].termVec);
            iterationCheck2 = compareCentroids(prevCentroids1, centroids[1].termVec);
            iteration++;
        }
    }

    /**
     * compare the centroids to check if it remains same from the previous centroids.
     * It determines if iteration needs to stop or continue
     */
    public boolean compareCentroids(double[] currentCentroid, double[] prevCentroid) {
        boolean check = false;
        for (int j = 0; j < vSize; j++) {
            if (currentCentroid[j] != prevCentroid[j]) {
                check = true;
                return check;
            }
        }
        return check;
    }


    /**
     * Calculate centroids from the updated clusters
     * For kmeans clustering, calculate the centroids among docuemtns in the same cluster
     */
    public Doc calcCentroid(ArrayList<Doc> cluster) {

        double[] vectorGross = new double[vSize];
        for (Doc doc : cluster) {
            for (int j = 0; j < vSize; j++) vectorGross[j] += doc.termVec[j];
        }
        for (int j = 0; j < vSize; j++) vectorGross[j] = vectorGross[j] / cluster.size();
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
        for (int i = 0; i < vSize; i++) {
            double doc1tf = doc1.termVec[i];
            double doc2tf = doc2.termVec[i];
            vectorDistance = vectorDistance + Math.abs(doc1tf - doc2tf);
        }
        return vectorDistance;
    }


    /**
     * Calculate distance between two documenets
     * For kmeans clustering, calculate the distance between two documents
     */
    public void hierarchicalClustering() {


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