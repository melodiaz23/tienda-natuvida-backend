package com.natuvida.store.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class PriceResponseDTO {
  private UUID id;
  private BigDecimal unit;
  private BigDecimal twoUnits;
  private BigDecimal threeUnits;
}