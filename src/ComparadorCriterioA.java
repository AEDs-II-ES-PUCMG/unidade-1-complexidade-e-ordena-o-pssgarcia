import java.util.Comparator;
import java.util.Map;

/**
 * Critério A - Valor Final do Pedido (crescente).
 * Desempate 1: Volume Total de Itens.
 * Desempate 2: Código Identificador do primeiro produto do pedido.
 */
public class ComparadorCriterioA implements Comparator<Pedido> {

    private final Map<Pedido, PedidoMetricas> cache;

    public ComparadorCriterioA(Map<Pedido, PedidoMetricas> cache) {
        this.cache = cache;
    }

    @Override
    public int compare(Pedido p1, Pedido p2) {
        PedidoMetricas m1 = PedidoMetricas.de(p1, cache);
        PedidoMetricas m2 = PedidoMetricas.de(p2, cache);

        int c1 = Double.compare(m1.valorFinal(), m2.valorFinal());
        if (c1 != 0) return c1;

        int c2 = Integer.compare(m1.volumeTotal(), m2.volumeTotal());
        if (c2 != 0) return c2;

        return Integer.compare(m1.codigoPrimeiroItem(), m2.codigoPrimeiroItem());
    }
}
