package com.learnit.learnit.utils;

public class IdWeightPair {

    public IdWeightPair(int id, float weight) {
        mId = id;
        mWeight = weight;
    }

    public int id() {
        return mId;
    }

    public float weight() {
        return mWeight;
    }

    public void setId(final int id) {
        mId = id;
    }

    public void setWeight(final float weight) {
        mWeight = weight;
    }

    @Override
    public String toString() {
        return String.format("[ id: %s, w: %s]", mId, mWeight);
    }

    private int mId;
    private float mWeight;
}
