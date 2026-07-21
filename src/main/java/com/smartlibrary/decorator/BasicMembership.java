package com.smartlibrary.decorator;
public class BasicMembership implements Membership {
    @Override public String getDescription() { return "Basico (max 3)"; }
    @Override public int getMaxBooks()       { return 3; }
}
