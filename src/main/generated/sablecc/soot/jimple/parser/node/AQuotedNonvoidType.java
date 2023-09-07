/* This file was generated by SableCC (http://www.sablecc.org/). */

package soot.jimple.parser.node;

import java.util.*;
import soot.jimple.parser.analysis.*;

@SuppressWarnings("nls")
public final class AQuotedNonvoidType extends PNonvoidType
{
    private TQuotedName _quotedName_;
    private final LinkedList<PArrayBrackets> _arrayBrackets_ = new LinkedList<PArrayBrackets>();

    public AQuotedNonvoidType()
    {
        // Constructor
    }

    public AQuotedNonvoidType(
        @SuppressWarnings("hiding") TQuotedName _quotedName_,
        @SuppressWarnings("hiding") List<?> _arrayBrackets_)
    {
        // Constructor
        setQuotedName(_quotedName_);

        setArrayBrackets(_arrayBrackets_);

    }

    @Override
    public Object clone()
    {
        return new AQuotedNonvoidType(
            cloneNode(this._quotedName_),
            cloneList(this._arrayBrackets_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAQuotedNonvoidType(this);
    }

    public TQuotedName getQuotedName()
    {
        return this._quotedName_;
    }

    public void setQuotedName(TQuotedName node)
    {
        if(this._quotedName_ != null)
        {
            this._quotedName_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._quotedName_ = node;
    }

    public LinkedList<PArrayBrackets> getArrayBrackets()
    {
        return this._arrayBrackets_;
    }

    public void setArrayBrackets(List<?> list)
    {
        for(PArrayBrackets e : this._arrayBrackets_)
        {
            e.parent(null);
        }
        this._arrayBrackets_.clear();

        for(Object obj_e : list)
        {
            PArrayBrackets e = (PArrayBrackets) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._arrayBrackets_.add(e);
        }
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._quotedName_)
            + toString(this._arrayBrackets_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._quotedName_ == child)
        {
            this._quotedName_ = null;
            return;
        }

        if(this._arrayBrackets_.remove(child))
        {
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._quotedName_ == oldChild)
        {
            setQuotedName((TQuotedName) newChild);
            return;
        }

        for(ListIterator<PArrayBrackets> i = this._arrayBrackets_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PArrayBrackets) newChild);
                    newChild.parent(this);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        throw new RuntimeException("Not a child.");
    }
}
