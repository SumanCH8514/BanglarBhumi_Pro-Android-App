export interface DistrictItem {
  code: string;
  en: string;
  bn: string;
  blockCount: number;
}

export interface BlockItem {
  code: string;
  en: string;
  bn: string;
  mouzaCount: number;
}

export interface MouzaItem {
  code: string;
  en: string;
  bn: string;
  mid?: string;
}

export interface GlobalMouzaSearchResult {
  districtCode: string;
  districtName: string;
  blockCode: string;
  blockName: string;
  mouzaCode: string;
  mouzaNameEn: string;
  mouzaNameBn: string;
}

export interface LandRecordMetadata {
  districtCode: string;
  districtName: string;
  blockCode: string;
  blockName: string;
  mouzaCode: string;
  mouzaName: string;
  searchType: string;
  searchNumber: string;
  source: string;
  timestamp: string;
}

export interface LandRecordResult {
  html: string;
  metadata: LandRecordMetadata;
}

export interface SavedRecordItem {
  id: string;
  savedAt: string;
  metadata: LandRecordMetadata;
  html: string;
}
