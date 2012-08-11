/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.MyBoolean;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.main.MyError;

/**
 * Take objects from the middle of a list
 * adapted from AlgoKeepIf
 * @author Michael Borcherds
 */

public class AlgoKeepIf3 extends AlgoElement {

	private GeoList inputList; //input
	private GeoList outputList; //output	
	private GeoBoolean bool;     // input
	private GeoElement var;
	private int size;

	/**
	 * @param cons construction
	 * @param label label 
	 * @param bool boolean filter (dependent on var)
	 * @param var variable to be substituted
	 * @param inputList list
	 */
	public AlgoKeepIf3(Construction cons, String label, GeoBoolean bool, GeoElement var, GeoList inputList) {
		super(cons);
		this.inputList = inputList;
		this.var = var;
		this.bool = bool;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoKeepIf3;
	}

	@Override
	protected void setInputOutput(){

		input = new GeoElement[3];
		input[0] = bool;
		input[1] = var;
		input[2] = inputList;

		super.setOutputLength(1);
		super.setOutput(0, outputList);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting list
	 */
	public GeoList getResult() {
		return outputList;
	}

	@Override
	public final void compute() {

		size = inputList.size();

		if (!inputList.isDefined()) {
			outputList.setUndefined();
			return;
		} 

		outputList.setDefined(true);
		outputList.clear();

		if (size == 0) return;

		try {
			for (int i=0 ; i<size ; i++) {
				GeoElement geo = inputList.get(i);

				ExpressionNode ex = (ExpressionNode) ((AlgoDependentBoolean)bool.getParentAlgorithm()).getExpression().deepCopy(kernel);
				ex = ex.replace(var, geo.evaluate(StringTemplate.defaultTemplate)).wrap();
				if (((MyBoolean)ex.evaluate(StringTemplate.defaultTemplate)).getBoolean()) {
					outputList.add(geo.copyInternal(cons));
				}
			}
		} catch (MyError e) {
			// eg KeepIf[x(A)<2,A,{(1,1),(2,2),(3,3),1}]
			e.printStackTrace();
			outputList.setUndefined();
			return;
		}

	} 	

	@Override
	public EquationElement buildEquationElementForGeo(GeoElement element,
			EquationScope scope) {
		return null;
	}

	@Override
	public boolean isLocusEquable() {
		return false;
	}

}
