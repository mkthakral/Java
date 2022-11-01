package folyoz.processor.pojo;

import org.apache.solr.client.solrj.beans.Field;

public class CustomProduct3 {
	private String sku;
	private String name;
	private String image;
	private String thumbnail;
	private int category;
	private String attributeStyle;
	private String attributeCategory;

	private int artistID;
	private String artistName;
	private String artistCity;
	private String artistState;
	private String artistCountry;
	private String artistGender;
	private String artistEthnicity;

	/**
	 * @param sku
	 * @param name
	 * @param image
	 * @param thumbnail
	 * @param category
	 * @param attributeStyle
	 * @param attributeCategory
	 * @param artistID
	 * @param artistName
	 * @param artistCity
	 * @param artistState
	 * @param artistCountry
	 * @param artistGender
	 * @param artistEthnicity
	 */
	public CustomProduct3(String sku, String name, String image, String thumbnail, int category, String attributeStyle,
			String attributeCategory, int artistID, String artistName, String artistCity, String artistState,
			String artistCountry, String artistGender, String artistEthnicity) {
		super();
		this.sku = sku;
		this.name = name;
		this.image = image;
		this.thumbnail = thumbnail;
		this.category = category;
		this.attributeStyle = attributeStyle;
		this.attributeCategory = attributeCategory;
		this.artistID = artistID;
		this.artistName = artistName;
		this.artistCity = artistCity;
		this.artistState = artistState;
		this.artistCountry = artistCountry;
		this.artistGender = artistGender;
		this.artistEthnicity = artistEthnicity;
	}

	/**
	 * @return the sku
	 */
	
	public String getSku() {
		return sku;
	}

	/**
	 * @param sku the sku to set
	 */
	public void setSku(String sku) {
		this.sku = sku;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	@Field("name")
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * @return the thumbnail
	 */
	public String getThumbnail() {
		return thumbnail;
	}

	/**
	 * @param thumbnail the thumbnail to set
	 */
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	/**
	 * @return the category
	 */
	public int getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(int category) {
		this.category = category;
	}

	/**
	 * @return the attributeStyle
	 */
	public String getAttributeStyle() {
		return attributeStyle;
	}

	/**
	 * @param attributeStyle the attributeStyle to set
	 */
	public void setAttributeStyle(String attributeStyle) {
		this.attributeStyle = attributeStyle;
	}

	/**
	 * @return the attributeCategory
	 */
	public String getAttributeCategory() {
		return attributeCategory;
	}

	/**
	 * @param attributeCategory the attributeCategory to set
	 */
	public void setAttributeCategory(String attributeCategory) {
		this.attributeCategory = attributeCategory;
	}

	/**
	 * @return the artistID
	 */
	public int getArtistID() {
		return artistID;
	}

	/**
	 * @param artistID the artistID to set
	 */
	public void setArtistID(int artistID) {
		this.artistID = artistID;
	}

	/**
	 * @return the artistName
	 */
	public String getArtistName() {
		return artistName;
	}

	/**
	 * @param artistName the artistName to set
	 */
	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	/**
	 * @return the artistCity
	 */
	public String getArtistCity() {
		return artistCity;
	}

	/**
	 * @param artistCity the artistCity to set
	 */
	public void setArtistCity(String artistCity) {
		this.artistCity = artistCity;
	}

	/**
	 * @return the artistState
	 */
	public String getArtistState() {
		return artistState;
	}

	/**
	 * @param artistState the artistState to set
	 */
	public void setArtistState(String artistState) {
		this.artistState = artistState;
	}

	/**
	 * @return the artistCountry
	 */
	public String getArtistCountry() {
		return artistCountry;
	}

	/**
	 * @param artistCountry the artistCountry to set
	 */
	public void setArtistCountry(String artistCountry) {
		this.artistCountry = artistCountry;
	}

	/**
	 * @return the artistGender
	 */
	public String getArtistGender() {
		return artistGender;
	}

	/**
	 * @param artistGender the artistGender to set
	 */
	public void setArtistGender(String artistGender) {
		this.artistGender = artistGender;
	}

	/**
	 * @return the artistEthnicity
	 */
	public String getArtistEthnicity() {
		return artistEthnicity;
	}

	/**
	 * @param artistEthnicity the artistEthnicity to set
	 */
	public void setArtistEthnicity(String artistEthnicity) {
		this.artistEthnicity = artistEthnicity;
	}

	public static String getFirstRow() {
		return "sku,name,image,thumbnail,cataegory,attributeStyle,attributeCategory,artistID,artistName,artistCity,artistState,artistCountry,artistGender,artistEthnicity";

	}

	public String toCSV() {
		return sku + "," + name + "," + image + "," + thumbnail + "," + category
				+ "," + attributeStyle + "\"," + attributeCategory + "\","
				+ artistID + "," + artistName + "," + artistCity + ","
				+ artistState + "," + artistCountry + "," + artistGender
				+ "," + artistEthnicity;
	}

}
