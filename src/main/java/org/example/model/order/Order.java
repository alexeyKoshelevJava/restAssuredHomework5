package org.example.model.order;

import com.google.gson.annotations.SerializedName;


import java.util.Objects;


public class Order{

	@SerializedName("petId")
	private  Integer petId;

	@SerializedName("quantity")
	private Integer quantity;

	@SerializedName("id")
	private   Integer id;

	@SerializedName("shipDate")
	private String shipDate;

	@SerializedName("complete")
	private Boolean complete;

	@SerializedName("status")
	private Status status;

	public Integer getId() {
		return id;
	}

	public Integer getPetId() {
		return petId;
	}

	public void setPetId(Integer petId) {
		this.petId = petId;
	}



	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}



	public void setId(Integer id) {
		this.id = id;
	}



	public void setShipDate(String shipDate) {
		this.shipDate = shipDate;
	}


	public void setComplete(Boolean complete) {
		this.complete = complete;
	}



	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Order order = (Order) o;
		return Objects.equals(petId, order.petId) && Objects.equals(quantity, order.quantity) && Objects.equals(id, order.id) && Objects.equals(shipDate, order.shipDate) && Objects.equals(complete, order.complete) && status == order.status;
	}

	@Override
	public int hashCode() {
		return Objects.hash(petId, quantity, id, shipDate, complete, status);
	}

	@Override
	public String toString() {
		return "Order{" +
				"petId=" + petId +
				", quantity=" + quantity +
				", id=" + id +
				", shipDate='" + shipDate + '\'' +
				", complete=" + complete +
				", status=" + status +
				'}';
	}
}