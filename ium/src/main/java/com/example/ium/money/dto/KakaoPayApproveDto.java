package com.example.ium.money.dto;

import lombok.Data;

@Data
public class KakaoPayApproveDto {
  private String aid;
  private String tid;
  private String partner_order_id;
  private String partner_user_id;
  private String item_name;
  private int quantity;
  private Amount amount;
  
  @Data
  private class Amount {
    private int total;
    private int tax_free;
    private int vat;
  }
  
  public int getTotalAmount() {
    return amount != null ? amount.total : 0;
  }
}
