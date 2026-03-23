package fr.esigelec.borne.model;

public class Station {
	private int id;
    private String nom_station;
    private String adresse;
    private String commune;
    private double lat;
    private double lon;
    private double puissance;
    private int nb_prises;
    private boolean gratuit;
    private boolean paiement_cb;
    private String tarif;
    private String acces;
    private String horaires;
    private boolean prise_ef;
    private boolean prise_t2;
    private boolean prise_ccs;
    private boolean prise_chademo;
    
    public Station() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNom_station() {
		return nom_station;
	}

	public void setNom_station(String nom_station) {
		this.nom_station = nom_station;
	}

	public String getAdresse() {
		return adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	public String getCommune() {
		return commune;
	}

	public void setCommune(String commune) {
		this.commune = commune;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getPuissance() {
		return puissance;
	}

	public void setPuissance(double puissance) {
		this.puissance = puissance;
	}

	public int getNb_prises() {
		return nb_prises;
	}

	public void setNb_prises(int nb_prises) {
		this.nb_prises = nb_prises;
	}

	public boolean isGratuit() {
		return gratuit;
	}

	public void setGratuit(boolean gratuit) {
		this.gratuit = gratuit;
	}

	public boolean isPaiement_cb() {
		return paiement_cb;
	}

	public void setPaiement_cb(boolean paiement_cb) {
		this.paiement_cb = paiement_cb;
	}

	public String getTarif() {
		return tarif;
	}

	public void setTarif(String tarif) {
		this.tarif = tarif;
	}

	public String getAcces() {
		return acces;
	}

	public void setAcces(String acces) {
		this.acces = acces;
	}

	public String getHoraires() {
		return horaires;
	}

	public void setHoraires(String horaires) {
		this.horaires = horaires;
	}

	public boolean isPrise_ef() {
		return prise_ef;
	}

	public void setPrise_ef(boolean prise_ef) {
		this.prise_ef = prise_ef;
	}

	public boolean isPrise_t2() {
		return prise_t2;
	}

	public void setPrise_t2(boolean prise_t2) {
		this.prise_t2 = prise_t2;
	}

	public boolean isPrise_ccs() {
		return prise_ccs;
	}

	public void setPrise_ccs(boolean prise_ccs) {
		this.prise_ccs = prise_ccs;
	}

	public boolean isPrise_chademo() {
		return prise_chademo;
	}

	public void setPrise_chademo(boolean prise_chademo) {
		this.prise_chademo = prise_chademo;
	}
    

}
