package com.hidarisoft.posentregamicroservice.client;


import com.hidarisoft.posentregamicroservice.config.FeignClientConfig;
import com.hidarisoft.posentregamicroservice.dto.AtualizacaoStatusPedidoDTO;
import com.hidarisoft.posentregamicroservice.dto.PedidoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "pedidos-service", url = "${pedidos.service.url}", configuration = FeignClientConfig.class)
public interface PedidoClient {

    @GetMapping("/pedidos/{id}")
    ResponseEntity<PedidoDTO> buscarPedidoPorId(@PathVariable("id") Long id);

    @PutMapping("/pedidos/{id}/status")
    ResponseEntity<PedidoDTO> atualizarStatusPedido(
            @PathVariable("id") Long id,
            @RequestBody AtualizacaoStatusPedidoDTO statusPedidoDTO);
}