package consumerpetshop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import consumerpetshop.controller.enums.TipoCargo;
import consumerpetshop.dto.LoginCreateDTO;
import consumerpetshop.dto.LoginDTO;
import consumerpetshop.entity.CargoEntity;
import consumerpetshop.entity.UsuarioEntity;
import consumerpetshop.exception.RegraDeNegocioException;
import consumerpetshop.repository.CargoRepository;
import consumerpetshop.repository.UsuarioRepository;
import consumerpetshop.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final ObjectMapper objectMapper;
    private final UsuarioRepository usuarioRepository;
    private final CargoRepository cargoRepository;
    public Optional<UsuarioEntity> findByUsername(String login){
        return usuarioRepository.findByUsername(login);
    }

    public LoginDTO cadastro(LoginCreateDTO loginCreateDTO) throws RegraDeNegocioException {
        verificaUsername(loginCreateDTO.getUsername());
        UsuarioEntity novoUser = createToEntity(loginCreateDTO);
        novoUser.setSenha(new Argon2PasswordEncoder().encode(loginCreateDTO.getSenha()));
        novoUser.setAtivo(true);

        novoUser.setCargos(Set.of(cargoRepository.findById(TipoCargo.USUARIO.getTipo()).get()));
        usuarioRepository.save(novoUser);

        return entityToDTO(novoUser);
    }

    public void verificaUsername(String username) throws RegraDeNegocioException {
        if(usuarioRepository.findByUsername(username).isPresent()) {
            throw new RegraDeNegocioException("Este usuário já existe!");
        }
    }

    private UsuarioEntity createToEntity(LoginCreateDTO dto) {
        return objectMapper.convertValue(dto, UsuarioEntity.class);
    }

    public LoginDTO entityToDTO(UsuarioEntity entity) {
        LoginDTO dto = objectMapper.convertValue(entity, LoginDTO.class);
        dto.setCargos(entity.getCargos().stream()
                .map(CargoEntity::getNome).toList());
        return dto;
    }
}
