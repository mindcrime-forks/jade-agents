/*****************************************************************
Copyright (C) 2004 Mooter Pty Ltd.

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation,
version 2.1 of the License.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

package stress.deadlock;


/**
 * <p>Title: MessageBean</p>
 * <p>Description: Bean used to pass information to an agent.</p>
 * @author Richard Heycock
 */
public class MessageBean {
	private int	n;
	private ConditionVariable condVar;

	/**
	 * @return Returns the condVar.
	 */
	public ConditionVariable getCondVar() {
		return condVar;
	}

	/**
	 * @param condVar The condVar to set.
	 */
	public void setCondVar(ConditionVariable condVar) {
		this.condVar = condVar;
	}

	/**
	 * @return Returns the n.
	 */
	public int getN() {
		return n;
	}

	/**
	 * @param n The n to set.
	 */
	public void setN(int n) {
		this.n = n;
	}
}