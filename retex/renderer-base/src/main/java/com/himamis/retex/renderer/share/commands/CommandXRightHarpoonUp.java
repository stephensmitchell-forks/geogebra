package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.XArrowAtom;

public class CommandXRightHarpoonUp extends Command1O1A {

	@Override
	public Atom newI(TeXParser tp, Atom a, Atom b) {
		return new XArrowAtom(b, a, XArrowAtom.Kind.RightHarpoonUp);
	}

	@Override
	public Command duplicate() {
		CommandXRightHarpoonUp ret = new CommandXRightHarpoonUp();
		ret.hasopt = hasopt;
		ret.option = option;
		return ret;
	}

}