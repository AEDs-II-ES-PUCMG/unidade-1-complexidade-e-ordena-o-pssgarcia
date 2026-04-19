import java.util.Comparator;
import java.util.Map;

/**
 * Critério B - Volume Total de Itens (crescente).
 * Desempate 1: Data do Pedido.
 * Desempate 2: Código Identificador do pedido.
 */
public class ComparadorCriterioB implements Comparator<Pedido> {

    private final Map<Pedido, PedidoMetricas> cache;

    public ComparadorCriterioB(Map<Pedido, PedidoMetricas> cache) {
        this.cache = cache;
    }

    @Override
    public int compare(Pedido p1, Pedido p2) {
        PedidoMetricas m1 = PedidoMetricas.de(p1, cache);
        PedidoMetricas m2 = PedidoMetricas.de(p2, cache);

        int c1 = Integer.compare(m1.volumeTotal(), m2.volumeTotal());
        if (c1 != 0) return c1;

        int c2 = Long.compare(m1.dataEpoch(), m2.dataEpoch());
        if (c2 != 0) return c2;

        return Integer.compare(m1.codigoPedido(), m2.codigoPedido());
    }
}
