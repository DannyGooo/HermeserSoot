/* This file was generated by SableCC (http://www.sablecc.org/). */

package soot.jimple.parser.node;

import soot.jimple.parser.analysis.*;

@SuppressWarnings("nls")
public final class AShortBaseTypeNoName extends PBaseTypeNoName
{
    private TShort _short_;

    public AShortBaseTypeNoName()
    {
        // Constructor
    }

    public AShortBaseTypeNoName(
        @SuppressWarnings("hiding") TShort _short_)
    {
        // Constructor
        setShort(_short_);

    }

    @Override
    public Object clone()
    {
        return new AShortBaseTypeNoName(
            cloneNode(this._short_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAShortBaseTypeNoName(this);
    }

    public TShort getShort()
    {
        return this._short_;
    }

    public void setShort(TShort node)
    {
        if(this._short_ != null)
        {
            this._short_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._short_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._short_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._short_ == child)
        {
            this._short_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._short_ == oldChild)
        {
            setShort((TShort) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
