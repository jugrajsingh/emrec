package edu.ubbcluj.emotion.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "actionunit")
public class ActionUnit implements Serializable, DatasetKey {

	private static final long	serialVersionUID	= 1L;

	@Id
	@GeneratedValue
	@Column(name = "actionunit_id")
	private Long				id;

	@Column(name = "code")
	private int					code;

	@Column(name = "intensity")
	private int					intensity;

	public ActionUnit() {
	}

	public ActionUnit(int code, int intensity) {
		this.code = code;
		this.intensity = intensity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getCode() {
		return code;
	}

	public int getIntensity() {
		return intensity;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setIntensity(int intensity) {
		this.intensity = intensity;
	}

	@Transient
	public String getDescription() {
		return ActionUnitDescription.getAUDescription(code);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + code;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActionUnit other = (ActionUnit) obj;
		if (code != other.code)
			return false;
		return true;
	}

}
