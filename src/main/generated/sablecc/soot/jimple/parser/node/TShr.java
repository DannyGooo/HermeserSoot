/* This file was generated by SableCC (http://www.sablecc.org/). */

package soot.jimple.parser.node;

import soot.jimple.parser.analysis.*;

@SuppressWarnings("nls")
public final class TShr extends Token
{
    public TShr()
    {
        super.setText(">>");
    }

    public TShr(int line, int pos)
    {
        super.setText(">>");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TShr(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTShr(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TShr text.");
    }
}
