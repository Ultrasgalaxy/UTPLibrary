package com.smartlibrary.decorator;
public abstract class MembershipDecorator implements Membership {
    protected Membership membership;
    public MembershipDecorator(Membership m) { this.membership = m; }
}
