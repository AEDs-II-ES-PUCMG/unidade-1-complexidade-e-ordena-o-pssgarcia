import java.util.Comparator;
import java.util.Map;

/**
 * Critério C - Índice de Economia (decrescente).
 * O índice de economia é a diferença entre o valor de catálogo atual e o valor efetivamente pago.
 * Desempate 1: Valor Final do Pedido (crescente).
 * Desempate 2: Código Identificador do pedido (crescente).
 */
public class ComparadorCriterioC implements Comparator<Pedido> {
    private final Map<Pedido, Double> cacheEconomia;
    private final Map<Pedido, Double> cacheValor;

    public ComparadorCriterioC(Map<Pedido, Double> cacheEconomia, Map<Pedido, Double> cacheValor) {
        this.cacheEconomia = cacheEconomia;
        this.cacheValor = cacheValor;
    }

    @Override
    public int compare(Pedido p1, Pedido p2) {
        //Sua lógica de comparação aqui
        // DECRESCENTE
        int c1 = -Double.compare(
                PedidoMetricas.economiaReal(p1, cacheEconomia),
                PedidoMetricas.economiaReal(p2, cacheEconomia));
        if (c1 != 0) return c1;

        int c2 = Double.compare(
                PedidoMetricas.valorFinal(p1, cacheValor),
                PedidoMetricas.valorFinal(p2, cacheValor));
        if (c2 != 0) return c2;

        return Integer.compare(PedidoMetricas.codigoPedido(p1), PedidoMetricas.codigoPedido(p2));
    }
}