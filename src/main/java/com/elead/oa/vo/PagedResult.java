package com.elead.oa.vo;

import java.util.List;

/**
 * 分页查询结果返回类
 * @author liwei
 *
 * @param <T> 泛型
 */
public class PagedResult<T> {
	private int total;
	private List<T> rows;
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public List<T> getRows() {
		return rows;
	}
	public void setRows(List<T> rows) {
		this.rows = rows;
	}
}
