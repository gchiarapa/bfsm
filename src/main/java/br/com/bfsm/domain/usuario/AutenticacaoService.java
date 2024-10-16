package br.com.bfsm.domain.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.bfsm.repository.UsuarioRepository;

@Service
public class AutenticacaoService implements UserDetailsService {
	
	@Autowired
	private UsuarioRepository usuarioRepo;

	@Override
	public Usuario loadUserByUsername(String username) throws UsernameNotFoundException {
		return usuarioRepo.findByLogin(username).get();
	}
	
	

}
