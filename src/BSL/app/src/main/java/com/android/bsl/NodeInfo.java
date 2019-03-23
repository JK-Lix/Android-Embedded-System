package com.android.bsl;

import java.io.Serializable;
import java.util.Arrays;

import android.os.Parcel;
import android.os.Parcelable;

import org.w3c.dom.Node;

public class NodeInfo implements Parcelable, Cloneable {

    private long lastRecordTime;
    private String id;
    private byte type;
    private String info;
    private String parentId;
    private String nodeName;
    private byte dengState;
    private byte mada_turnto;
    private int mada_sudu;
    private float[] values;
    private float value;

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public Object clone() {
        NodeInfo nodeInfo = null;
        try {
            nodeInfo = (NodeInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return nodeInfo;
    }

    public void setValues(float[] values) {
        this.values = values;
    }

    public float[] getValues() {
        return values;
    }

    public long getLastRecordTime() {
        return lastRecordTime;
    }

    public void setLastRecordTime(long lastRecordTime) {
        this.lastRecordTime = lastRecordTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }


    public byte getDengState() {
        return dengState;
    }

    public void setDengState(byte dengState) {
        this.dengState = dengState;
    }


    public byte getMada_turnto() {
        return mada_turnto;
    }

    public void setMada_turnto(byte mada_turnto) {
        this.mada_turnto = mada_turnto;
    }

    public int getMada_sudu() {
        return mada_sudu;
    }

    public void setMada_sudu(int mada_sudu) {
        this.mada_sudu = mada_sudu;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o.getClass() == NodeInfo.class) {
            NodeInfo node = (NodeInfo) o;
            return this.id.equals(node.id);
        }
        return false;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel arg0, int arg1) {
        arg0.writeLong(lastRecordTime);
        arg0.writeString(id);
        arg0.writeByte(type);
        arg0.writeString(info);
        arg0.writeString(parentId);
        arg0.writeString(nodeName);
        arg0.writeByte(dengState);

    }

    public static final Parcelable.Creator<NodeInfo> CREATOR = new Parcelable.Creator<NodeInfo>() {

        @Override
        public NodeInfo createFromParcel(Parcel source) {
            NodeInfo p = new NodeInfo();

            p.lastRecordTime = source.readLong();
            p.id = source.readString();
            p.type = source.readByte();
            p.info = source.readString();
            p.parentId = source.readString();
            p.nodeName = source.readString();
            p.dengState = source.readByte();
            return p;
        }

        @Override
        public NodeInfo[] newArray(int size) {
            // TODO Auto-generated method stub
            return null;
        }
    };

    @Override
    public String toString() {
        return "NodeInfo{" +
                "lastRecordTime=" + lastRecordTime +
                ", id='" + id + '\'' +
                ", type=" + type +
                ", info='" + info + '\'' +
                ", parentId='" + parentId + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", dengState=" + dengState +
                ", mada_turnto=" + mada_turnto +
                ", mada_sudu=" + mada_sudu +
                ", values=" + Arrays.toString(values) +
                ", value=" + value +
                '}';
    }
}
