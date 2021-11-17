package com.example.sjtu_network.syno;

import com.example.sjtu_network.dict.Ws;

import java.util.List;

public class Syno {
    private String tran;
    private List<Ws> wss;
    private String pos;


    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public List<Ws> getWss() {
        return wss;
    }

    public void setWss(List<Ws> wss) {
        this.wss = wss;
    }

    public String getTran() {
        return tran;
    }

    public void setTran(String tran) {
        this.tran = tran;
    }
}
