package com.brandon.spring_api_restaurant_demo.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.brandon.spring_api_restaurant_demo.enums.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "client_id", nullable = false)
	Client client;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	OrderStatus orderStatus;

	@Column
	LocalDateTime createdAt;

	@Column
	LocalDateTime updatedAt;

	@Column
	boolean active;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	List<OrderItem> items = new ArrayList<>();

	// Historial de estados
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<OrderStatusHistory> statusHistory = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}

	public List<OrderStatusHistory> getStatusHistory() {
		return statusHistory;
	}

	public void setStatusHistory(List<OrderStatusHistory> statusHistory) {
		this.statusHistory = statusHistory;
	}

	public void addItem(OrderItem item) {
		items.add(item);
		item.setOrder(this);
	}

	public void addStatusHistory(OrderStatusHistory history) {
		statusHistory.add(history);
		history.setOrder(this);
	}

}
