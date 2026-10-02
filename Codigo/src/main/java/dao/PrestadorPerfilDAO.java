package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.PrestadorPerfil;

public class PrestadorPerfilDAO extends DAO {

    public PrestadorPerfilDAO() {
        super();
    }

    public PrestadorPerfil buscarPerfilPorId(long prestadorId) {
        String sql = "SELECT " +
                     "p.id AS prestador_id, " +
                     "u.id AS usuario_id, " +
                     "u.nome, " +
                     "u.telefone, " +
                     "u.cidade, " +
                     "u.bairro, " +
                     "u.rua, " +
                     "p.descricao, " +
                     "COALESCE(STRING_AGG(c.nome, ', '), 'Sem categoria') AS categorias " +
                     "FROM prestadores p " +
                     "INNER JOIN usuarios u ON u.id = p.usuarios_id " +
                     "LEFT JOIN prestadores_categorias pc ON pc.prestadores_id = p.id " +
                     "LEFT JOIN categorias c ON c.id = pc.categorias_id " +
                     "WHERE p.id = ? " +
                     "GROUP BY p.id, u.id, u.nome, u.telefone, u.cidade, u.bairro, u.rua, p.descricao";

        try (PreparedStatement ps = db.prepareStatement(sql)) {
            ps.setLong(1, prestadorId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PrestadorPerfil p = new PrestadorPerfil();

                    p.setPrestadorId(rs.getLong("prestador_id"));
                    p.setUsuarioId(rs.getLong("usuario_id"));
                    p.setNome(rs.getString("nome"));
                    p.setTelefone(rs.getString("telefone"));
                    p.setCidade(rs.getString("cidade"));
                    p.setBairro(rs.getString("bairro"));
                    p.setRua(rs.getString("rua"));
                    p.setDescricao(rs.getString("descricao"));
                    p.setCategorias(rs.getString("categorias"));

                    return p;
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar perfil do prestador: " + e.getMessage());
        }

        return null;
    }
}