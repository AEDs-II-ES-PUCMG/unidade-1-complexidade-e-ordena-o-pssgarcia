import java.util.Comparator;
import java.util.Map;

/**
 * Critério B - Volume Total de Itens (crescente).
 * Desempate 1: Data do Pedido.
 * Desempate 2: Código Identificador do pedido.
 */
public class ComparadorCriterioB implements Comparator<Pedido> {
    private final Map<Pedido, Integer> cacheVolume;

    public ComparadorCriterioB(Map<Pedido, Integer> cacheVolume) {
        this.cacheVolume = cacheVolume;
    }

    @Override
    public int compare(Pedido p1, Pedido p2) {
        //Sua lógica de comparação aqui
        int c1 = Integer.compare(
                PedidoMetricas.volumeTotal(p1, cacheVolume),
                PedidoMetricas.volumeTotal(p2, cacheVolume));
        if (c1 != 0) return c1;

        int c2 = Long.compare(PedidoMetricas.dataEpoch(p1), PedidoMetricas.dataEpoch(p2));
        if (c2 != 0) return c2;

        return Integer.compare(PedidoMetricas.codigoPedido(p1), PedidoMetricas.codigoPedido(p2));
    }
}
