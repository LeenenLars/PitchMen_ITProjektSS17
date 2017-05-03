package de.pitchMen.shared.report;

/**
 * Subklasse von Paragraph. Einzelne Abs�tze werden als String gespeichert. Das
 * Einf�gen von Formatiertungssymbolen ist nicht erw�nscht.
 * 
 * @author
 */
public class SimpleParagraph extends Paragraph {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private String text = "";

	/**
	 * @return
	 */
	public String toString() {
		
		return this.text;
	}

	/**
	 * @return
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * @param value
	 */
	public void setText(String v) {
		this.text = v; 
	}

}
