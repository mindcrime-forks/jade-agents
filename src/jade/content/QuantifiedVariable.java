/**
 * ***************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop
 * multi-agent systems in compliance with the FIPA specifications.
 * Copyright (C) 2000 CSELT S.p.A.
 * 
 * GNU Lesser General Public License
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * **************************************************************
 */
package jade.content;

import jade.content.onto.*;

/**
 * @author Federico Bergenti - Universita` di Parma
 */
public class QuantifiedVariable extends Concept {
    private String   kind = null;
    private Variable variable = null;

    /**
     * Constructor
     *
     */
    public QuantifiedVariable() {}

    /**
     * Sets the kind of the variable, i.e., its cardinality.
     *
     * @param kind the kind.
     *
     */
    public void setKind(String kind) {
        this.kind = kind;
    } 

    /**
     * Sets the kind of the variable, i.e., its cardinality.
     *
     * @return the kind.
     *
     */
    public String getKind() {
        return kind;
    } 

    /**
     * Sets the variable.
     *
     * @param variable the variable
     *
     */
    public void setVariable(Variable variable) {
        this.variable = variable;
    } 

    /**
     * Gets the variable.
     *
     * @return the variable.
     *
     */
    public Variable getVariable() {
        return variable;
    } 

}

