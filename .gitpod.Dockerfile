FROM gitpod/workspace-base

USER root

# Install Nix
RUN addgroup --system nixbld \
  && adduser gitpod nixbld \
  && for i in $(seq 1 30); do useradd -ms /bin/bash nixbld$i &&  adduser nixbld$i nixbld; done \
  && mkdir -m 0755 /nix && chown gitpod /nix \
  && mkdir -p /etc/nix && echo 'sandbox = false' > /etc/nix/nix.conf
  
# Install Nix
CMD /bin/bash -l
USER gitpod
ENV USER gitpod
WORKDIR /home/gitpod

RUN touch .bash_profile \
 && curl https://nixos.org/releases/nix/nix-2.3.15/install | sh

RUN echo '. /home/gitpod/.nix-profile/etc/profile.d/nix.sh' >> /home/gitpod/.bashrc
RUN mkdir -p /home/gitpod/.bashrc.d
RUN mkdir -p /home/gitpod/.config/nixpkgs && echo '{ allowUnfree = true; }' >> /home/gitpod/.config/nixpkgs/config.nix

# Install cachix
RUN . /home/gitpod/.nix-profile/etc/profile.d/nix.sh \
  && nix-env -iA cachix -f https://cachix.org/api/v1/install \
  && cachix use cachix

# Install git
RUN . /home/gitpod/.nix-profile/etc/profile.d/nix.sh \
  && nix-env -i git git-lfs

# Install direnv
RUN . /home/gitpod/.nix-profile/etc/profile.d/nix.sh \
  && nix-env -i direnv \
  && direnv hook bash >> /home/gitpod/.bashrc

# Install any-nix-shell
RUN . /home/gitpod/.nix-profile/etc/profile.d/nix.sh \
  && nix-env -i any-nix-shell -f https://github.com/NixOS/nixpkgs/archive/master.tar.gz \
  && echo 'any-nix-shell zsh --info-right | . /dev/stdin' >> /home/gitpod/.zshrc
