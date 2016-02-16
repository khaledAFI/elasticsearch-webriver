package com.klead.es.river.data;

import java.util.Date;

/**
 * Created by kafi on 10/02/2016.
 */
public class Package extends Document{


    private Long id;
    private Integer idLogement;
    private Integer dureeJours;
    private Integer dureeNuits;
    private String villeDepart;
    private String pension;
    private Long prix;
    private Long prixj30;
    private Long offreCompleteId;
    private Integer dispo;
    private Integer ridTourOperateur;
    private Date dateCreation;
    private Date dateMAJ;
    private Boolean coupDeCoeur;
    private Boolean stopAffaire;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIdLogement() {
        return idLogement;
    }

    public void setIdLogement(Integer idLogement) {
        this.idLogement = idLogement;
    }

    public Integer getDureeJours() {
        return dureeJours;
    }

    public void setDureeJours(Integer dureeJours) {
        this.dureeJours = dureeJours;
    }

    public Integer getDureeNuits() {
        return dureeNuits;
    }

    public void setDureeNuits(Integer dureeNuits) {
        this.dureeNuits = dureeNuits;
    }

    public String getVilleDepart() {
        return villeDepart;
    }

    public void setVilleDepart(String villeDepart) {
        this.villeDepart = villeDepart;
    }

    public String getPension() {
        return pension;
    }

    public void setPension(String pension) {
        this.pension = pension;
    }

    public Long getPrix() {
        return prix;
    }

    public void setPrix(Long prix) {
        this.prix = prix;
    }

    public Long getPrixj30() {
        return prixj30;
    }

    public void setPrixj30(Long prixj30) {
        this.prixj30 = prixj30;
    }

    public Long getOffreCompleteId() {
        return offreCompleteId;
    }

    public void setOffreCompleteId(Long offreCompleteId) {
        this.offreCompleteId = offreCompleteId;
    }

    public Integer getDispo() {
        return dispo;
    }

    public void setDispo(Integer dispo) {
        this.dispo = dispo;
    }

    public Integer getRidTourOperateur() {
        return ridTourOperateur;
    }

    public void setRidTourOperateur(Integer ridTourOperateur) {
        this.ridTourOperateur = ridTourOperateur;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Date getDateMAJ() {
        return dateMAJ;
    }

    public void setDateMAJ(Date dateMAJ) {
        this.dateMAJ = dateMAJ;
    }


    public Boolean getCoupDeCoeur() {
        return coupDeCoeur;
    }

    public void setCoupDeCoeur(Boolean coupDeCoeur) {
        this.coupDeCoeur = coupDeCoeur;
    }

    public Boolean getStopAffaire() {
        return stopAffaire;
    }

    public void setStopAffaire(Boolean stopAffaire) {
        this.stopAffaire = stopAffaire;
    }
}
