package epub4j.domain;

public enum ManifestItemProperties implements ManifestProperties {
  COVER_IMAGE("cover-image"),
  MATHML("mathml"),
  NAV("nav"),
  REMOTE_RESOURCES("remote-resources"),
  SCRIPTED("scripted"),
  SVG("svg"),
  SWITCH("switch");

  private String name;

  ManifestItemProperties(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
