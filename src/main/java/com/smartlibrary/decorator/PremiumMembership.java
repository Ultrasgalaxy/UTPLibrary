package com.smartlibrary.decorator;
public class PremiumMembership extends MembershipDecorator {
    public PremiumMembership(Membership m) { super(m); }
    @Override public String getDescription() { return membership.getDescription() + " + Premium"; }
    @Override public int getMaxBooks()       { return membership.getMaxBooks() + 5; }
}
