import java.util.Comparator;
import java.util.Map;

/**
 * Critério C - Índice de Economia (decrescente).
 * Desempate 1: Valor Final do Pedido (crescente).
 * Desempate 2: Código Identificador do pedido (crescente).
 */
public class ComparadorCriterioC implements Comparator<Pedido> {

    private final Map<Pedido, PedidoMetricas> cache;

    public ComparadorCriterioC(Map<Pedido, PedidoMetricas> cache) {
        this.cache = cache;
    }

    @Override
    public int compare(Pedido p1, Pedido p2) {
        PedidoMetricas m1 = PedidoMetricas.de(p1, cache);
        PedidoMetricas m2 = PedidoMetricas.de(p2, cache);

        // DECRESCENTE: inverte a comparação da economia
        int c1 = -Double.compare(m1.economiaReal(), m2.economiaReal());
        if (c1 != 0) return c1;

        int c2 = Double.compare(m1.valorFinal(), m2.valorFinal());
        if (c2 != 0) return c2;

        return Integer.compare(m1.codigoPedido(), m2.codigoPedido());
    }
}